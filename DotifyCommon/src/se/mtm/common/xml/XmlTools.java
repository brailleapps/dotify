package se.mtm.common.xml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XmlTools {

	public static void transform(File input, File output, InputStream xsltFile, Map<String, Object> params) throws XMLToolsException {
		StreamSource xslt = new StreamSource(xsltFile);

		Transformer transformer;
		try {
			transformer = TransformerFactory.newInstance().newTransformer(xslt);
		} catch (TransformerConfigurationException e) {
			throw new XMLToolsException(e);
		} catch (TransformerFactoryConfigurationError e) {
			throw new XMLToolsException(e);
		}

		for (String name : params.keySet()) {
			transformer.setParameter(name, params.get(name));
		}

		StreamSource source = new StreamSource(input);
		StreamResult result = new StreamResult(output);

		try {
			transformer.transform(source, result);
		} catch (TransformerException e) {
			throw new XMLToolsException(e);
		}
	}

	public final static boolean isWellformedXML(File f) throws XMLToolsException {
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser saxParser = null;
		try {
			saxParser = factory.newSAXParser();
		} catch (ParserConfigurationException e) {
			throw new XMLToolsException("Failed to set up XML parser.", e);
		} catch (SAXException e) {
			throw new XMLToolsException("Failed to set up XML parser.", e);
		}
		DefaultHandler dh = new DefaultHandler();
		try {
			saxParser.parse(f, dh);
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			throw new XMLToolsException(e);
		}
		return true;
	}

}
