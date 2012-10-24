package org.daisy.dotify.setups.sv_SE.tasks;

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

import org.daisy.dotify.book.Row;
import org.daisy.dotify.formatter.utils.TextBorder;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.util.xml.catalog.CatalogEntityResolver;
import org.daisy.util.xml.catalog.CatalogExceptionNotRecoverable;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


@SuppressWarnings("deprecation")
public class SwedishVolumeCoverPage implements VolumeCoverPage {
	private BrailleTranslator filters;
	private String title;
	private ArrayList<String> creator;
	//private int rows;
	//private int cols;
	private TextBorder tb;
	//private int height;

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
    		voltext = "Volym "+intToText(volumeNo)+" av "+intToText(volumeCount);
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
	/*
    private String loc(int value) {
    	switch (value) {
    		case 0: return "noll"; 
    		case 1: return "ett";
    		case 2: return "två";
    		case 3: return "tre";
    		case 4: return "fyra";
    		case 5: return "fem";
    		case 6: return "sex";
    		case 7: return "sju";
    		case 8: return "åtta";
    		case 9: return "nio";
    		case 10: return "tio";
    		case 11: return "elva";
    		case 12: return "tolv";
    		case 13: return "tretton";
    		case 14: return "fjorton";
    		case 15: return "femton";
    		case 16: return "sexton";
    		case 17: return "sjutton";
    		case 18: return "arton";
    		case 19: return "nitton";
    		case 20: return "tjugo"; 
    	}
    	return ""+value;
    }*/

    public static String intToText(int value) {
    	if (value<0) return "minus " + intToText(-value);
    	switch (value) {
			case 0: return "noll"; 
			case 1: return "ett";
			case 2: return "två";
			case 3: return "tre";
			case 4: return "fyra";
			case 5: return "fem";
			case 6: return "sex";
			case 7: return "sju";
			case 8: return "åtta";
			case 9: return "nio";
			case 10: return "tio";
			case 11: return "elva";
			case 12: return "tolv";
			case 13: return "tretton";
			case 14: return "fjorton";
			case 15: return "femton";
			case 16: return "sexton";
			case 17: return "sjutton";
			case 18: return "arton";
			case 19: return "nitton";
    		case 20: return "tjugo";
    		case 30: return "trettio";
    		case 40: return "fyrtio";
    		case 50: return "femtio";
    		case 60: return "sextio";
    		case 70: return "sjuttio";
    		case 80: return "åttio";
    		case 90: return "nittio";
    	}
    	String pre = "";
    	if (value>=1000) {
    		pre = intToText(value / 1000) + "tusen";
    		value = value % 1000;
    	}
    	if (value>=100) {
    		pre = pre + (value>=200?intToText(value / 100):"") + "hundra";
    		value = value % 100;
    	}
    	if (value==0) return pre;
    	if (value<20) {
    		return pre + intToText(value);
    	} else {
        	int t = value % 10;
        	int r = (value / 10) * 10;
    		return pre + intToText(r) + (t>0?intToText(t):"");
    	}
    }

}
