package org.daisy.dotify.tools;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class XMLTools {

	public final static boolean isWellformedXML(File f) throws IOException, XMLToolsException {
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
			throw e;
		}
		return true;
	}

}
