package org.daisy.dotify.engine.impl;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.transform.TransformerFactory;
import javax.xml.xpath.XPathFactory;

import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.MarkerProcessorFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;

public class FactoryManager {
	private FormatterFactory formatterFactory;
	private MarkerProcessorFactoryMakerService markerProcessorFactory;
	private TextBorderFactoryMakerService textBorderFactory;
	private ExpressionFactory expressionFactory;
	private XMLEventFactory xmlEventFactory;
	private XMLOutputFactory xmlOutputFactory;
	private XMLInputFactory xmlInputFactory;
	private DocumentBuilderFactory documentBuilderFactory;
	private XPathFactory xpathFactory;
	private TransformerFactory transformerFactory;

	public FactoryManager() {
	}

	public FormatterFactory getFormatterFactory() {
		return formatterFactory;
	}

	public void setFormatterFactory(FormatterFactory formatterFactory) {
		this.formatterFactory = formatterFactory;
	}

	public MarkerProcessorFactoryMakerService getMarkerProcessorFactory() {
		return markerProcessorFactory;
	}

	public void setMarkerProcessorFactory(MarkerProcessorFactoryMakerService markerProcessorFactory) {
		this.markerProcessorFactory = markerProcessorFactory;
	}

	public TextBorderFactoryMakerService getTextBorderFactory() {
		return textBorderFactory;
	}

	public void setTextBorderFactory(TextBorderFactoryMakerService textBorderFactory) {
		this.textBorderFactory = textBorderFactory;
	}

	public ExpressionFactory getExpressionFactory() {
		return expressionFactory;
	}

	public void setExpressionFactory(ExpressionFactory expressionFactory) {
		this.expressionFactory = expressionFactory;
	}

	public XMLEventFactory getXmlEventFactory() {
		if (xmlEventFactory==null) {
			setXmlEventFactory(XMLEventFactory.newInstance());
		}
		return xmlEventFactory;
	}

	public void setXmlEventFactory(XMLEventFactory xmlEventFactory) {
		this.xmlEventFactory = xmlEventFactory;
	}

	public XMLOutputFactory getXmlOutputFactory() {
		if (xmlOutputFactory==null) {
			setXmlOutputFactory(XMLOutputFactory.newInstance());
		}
		return xmlOutputFactory;
	}

	public void setXmlOutputFactory(XMLOutputFactory xmlOutputFactory) {
		this.xmlOutputFactory = xmlOutputFactory;
	}

	public XMLInputFactory getXmlInputFactory() {
		if (xmlInputFactory==null) {
			XMLInputFactory in = XMLInputFactory.newInstance();
			in.setProperty(XMLInputFactory.IS_COALESCING, Boolean.TRUE);
			in.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, Boolean.TRUE);
			in.setProperty(XMLInputFactory.SUPPORT_DTD, Boolean.FALSE);
			in.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, Boolean.FALSE);
			setXmlInputFactory(in);
		}
		return xmlInputFactory;
	}

	public void setXmlInputFactory(XMLInputFactory xmlInputFactory) {
		this.xmlInputFactory = xmlInputFactory;
	}

	public DocumentBuilderFactory getDocumentBuilderFactory() {
		if (documentBuilderFactory==null) {
			setDocumentBuilderFactory(DocumentBuilderFactory.newInstance());
		}
		return documentBuilderFactory;
	}

	public void setDocumentBuilderFactory(DocumentBuilderFactory documentBuilderFactory) {
		this.documentBuilderFactory = documentBuilderFactory;
	}

	public XPathFactory getXpathFactory() {
		if (xpathFactory==null) {
			setXpathFactory(XPathFactory.newInstance());
		}
		return xpathFactory;
	}

	public void setXpathFactory(XPathFactory xpathFactory) {
		this.xpathFactory = xpathFactory;
	}

	public TransformerFactory getTransformerFactory() {
		if (transformerFactory==null) {
			setTransformerFactory(TransformerFactory.newInstance());
		}
		return transformerFactory;
	}

	public void setTransformerFactory(TransformerFactory transformerFactory) {
		this.transformerFactory = transformerFactory;
	}

}
