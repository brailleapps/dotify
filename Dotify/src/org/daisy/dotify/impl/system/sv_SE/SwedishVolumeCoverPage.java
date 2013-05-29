package org.daisy.dotify.impl.system.sv_SE;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.Integer2Text;
import org.daisy.dotify.text.Integer2TextFactoryMaker;
import org.daisy.dotify.text.IntegerOutOfRange;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


@SuppressWarnings("deprecation")
class SwedishVolumeCoverPage implements VolumeCoverPage {
	private BrailleTranslator filters;
	private String title;
	private ArrayList<String> creator;
	//private int rows;
	//private int cols;
	private TextBorder tb;
	//private int height;
	private final Integer2Text i2t;

	public SwedishVolumeCoverPage(File dtbook, TextBorder tb, BrailleTranslator filters) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilder docBuilder = initDocumentBuilder();
		Document d = docBuilder.parse(dtbook);
		XPath xp = XPathFactory.newInstance().newXPath();
		this.title = xp.evaluate("/dtbook/book/frontmatter/doctitle", d);
		org.w3c.dom.NodeList ns = (org.w3c.dom.NodeList)xp.evaluate("/dtbook/book/frontmatter/docauthor", d, XPathConstants.NODESET);
		this.creator = new ArrayList<String>();
		for (int i=0; i<ns.getLength(); i++) {
			creator.add(ns.item(i).getTextContent());
		}
		this.tb = tb;
		this.filters = filters;
		//this.height = height;
		try {
			i2t = Integer2TextFactoryMaker.newInstance().newInteger2Text(FilterLocale.parse("sv-SE"));
		} catch (UnsupportedLocaleException e) {
			// throw runtime exception here, this is okay because it's only
			// temporary, the entire implementation is about to be retired. No
			// use to clean up the throws clause
			throw new RuntimeException(e);
		}
	}
	
	protected DocumentBuilder initDocumentBuilder() throws ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		try {
			db.setEntityResolver(CatalogEntityResolver.getInstance());
		} catch (CatalogExceptionNotRecoverable e) {
			ParserConfigurationException pce = new ParserConfigurationException("Unable to set CatalogEntityResolver");
			pce.initCause(e);
			throw pce;
		}
		return db;
	}
	
	public ArrayList<Row> buildPage(int volumeNo, int volumeCount, int pageHeight) {

    	ArrayList<Row> ret = new ArrayList<Row>();
    	ret.add(new Row(tb.getTopBorder()));
    	for (int i=0; i<3; i++) {
    		ret.add(new Row(tb.addBorderToRow("")));
    	}

    	// add title
    	if (title!=null && title.length()>0) {
	    	for (String s : tb.addBorderToParagraph(filters.translate(title))) {
	    		ret.add(new Row(s));
	    	}
   			ret.add(new Row(tb.addBorderToRow("")));
    	}
    	
    	// add authors
    	if (creator.size()>3) {
    		for (String s : tb.addBorderToParagraph(filters.translate(creator.get(0) + " m.fl."))) {
    			ret.add(new Row(s));
    		}
    	} else {
	    	for (String c : creator) {
	    		if (c!=null && c.length()>0) {
		    		for (String s : tb.addBorderToParagraph(filters.translate(c))) {
		    			ret.add(new Row(s));
		    		}
	    		}
	    	}
    	}

    	// add volume number
    	String voltext;
    	if (volumeCount==1) {
    		voltext = "En volym";
    	} else {
			try {
				voltext = "Volym " + i2t.intToText(volumeNo) + " av " + i2t.intToText(volumeCount);
			} catch (IntegerOutOfRange e) {
				// throw runtime exception here, this is okay because it's only
				// temporary, the entire implementation is about to be retired.
				throw new RuntimeException(e);
			}
    	}
    	filters.setHyphenating(false);
    	ArrayList<String> vol = tb.addBorderToParagraph(filters.translate(voltext));
    	filters.setHyphenating(true);
    	while (ret.size()<pageHeight-vol.size()-1) {
    		ret.add(new Row(tb.addBorderToRow("")));
    	}
    	for (String s : vol) {
    		ret.add(new Row(s));
    	}
    	ret.add(new Row(tb.getBottomBorder()));
    	if (ret.size()>pageHeight) {
    		throw new RuntimeException("Unable to perform layout. Title page contains too many rows.");
    	}
    	return ret;

    }

}
