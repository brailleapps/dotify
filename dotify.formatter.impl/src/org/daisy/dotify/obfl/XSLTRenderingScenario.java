package org.daisy.dotify.obfl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Transformer;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.daisy.dotify.api.formatter.FormatterSequence;
import org.daisy.dotify.api.formatter.RenderingScenario;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.obfl.Expression;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class XSLTRenderingScenario implements RenderingScenario {
	private final ObflParser parser;
	private final Transformer t;
	private final Node node;
	private final TextProperties tp;
	private final Expression ev;
	private final String exp;
	private final static Logger logger = Logger.getLogger(XSLTRenderingScenario.class.getCanonicalName());

	public XSLTRenderingScenario(ObflParser parser, Transformer t, Node node, TextProperties tp, Expression ev, String exp) {
		this.parser = parser;
		this.t = t;
		this.node = node;
		this.tp = tp;
		this.ev = ev;
		this.exp = exp;
	}

	@Override
	public void renderScenario(FormatterSequence formatter) {
		try {
			ByteArrayOutputStream os = new ByteArrayOutputStream();
			t.transform(new DOMSource(node), new StreamResult(os));
			
			//TODO: could event reader report the errors reported by the dom parser? Specifically, more than one root node.
			newDocumentFromInputStream(new ByteArrayInputStream(os.toByteArray()), parser.getFactoryManager().getDocumentBuilderFactory());

			//render
			XMLInputFactory factory =  parser.getFactoryManager().getXmlInputFactory();
			XMLEventReader input = factory.createXMLEventReader(new ByteArrayInputStream(os.toByteArray()));
			XMLEvent event;
			while (input.hasNext()) {
				event = input.nextEvent();
				if (ObflParser.equalsStart(event, ObflQName.XML_PROCESSOR_RESULT)) {
					// ok
				} else if (event.isCharacters()) {
					formatter.addChars(event.asCharacters().getData(), tp);
				} else if (ObflParser.equalsStart(event, ObflQName.BLOCK)) {
					parser.parseBlock(event, input, formatter, tp);
				} else if (ObflParser.equalsStart(event, ObflQName.TABLE)) {
					parser.parseTable(event, input, formatter, tp);
				} else if (parser.processAsBlockContents(formatter, event, input, tp)) {
					//done
				} else if (ObflParser.equalsEnd(event, ObflQName.XML_PROCESSOR_RESULT)) {
					break;
				}
				else {
					ObflParser.report(event);
				}
			}
		} catch (Exception e) {
			//FIXME:do something
		}
	}
	
	  public static Document newDocumentFromInputStream(InputStream in, DocumentBuilderFactory factory) {
		    DocumentBuilder builder = null;
		    Document ret = null;

		    try {
		      builder = factory.newDocumentBuilder();
		      builder.setErrorHandler(new ErrorHandler() {

				@Override
				public void warning(SAXParseException exception) throws SAXException {
					logger.log(Level.WARNING, "a", exception);
				}

				@Override
				public void error(SAXParseException exception) throws SAXException {
					logger.log(Level.WARNING, "b", exception);
				}

				@Override
				public void fatalError(SAXParseException exception) throws SAXException { 
					logger.log(Level.SEVERE, "A fatal error occurred when reading xml.", exception);
				}});
		    } catch (ParserConfigurationException e) {
		      e.printStackTrace();
		    }

		    try {
		      ret = builder.parse(new InputSource(in));
		    } catch (SAXException e) {
		      e.printStackTrace();
		    } catch (IOException e) {
		      e.printStackTrace();
		    }
		    return ret;
	  }

	@Override
	public double calculateCost(Map<String, Double> variables) {
		for (String key : variables.keySet()) {
			ev.setVariable(key, ((Double)variables.get(key)));
		}
		return Double.parseDouble(ev.evaluate(exp).toString());
	}
}