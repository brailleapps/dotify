package org.daisy.dotify.devtools.schematron;

import java.io.Closeable;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Provides compact java notation for common Schematron patterns.
 * @author Joel Håkansson
 *
 */
public class SchematronRulesGenerator implements Closeable {
	public final static String RULE_PREFIX="";

	private int rule_no;
	private PrintStream ps;
	public final String nsPrefix;
	public final String nsUri;

	/**
	 * Creates a new SchematronRulesGenerator with the supplied parameters
	 * @param title title of the rule set
	 * @param nsPrefix namespace prefix for the input document
	 * @param nsUri namespace uri for the input document
	 * @param os output stream
	 * @throws UnsupportedEncodingException
	 */
	public SchematronRulesGenerator(String title, String nsPrefix, String nsUri, OutputStream os) throws UnsupportedEncodingException {
		this.rule_no = 1;
		this.ps = new PrintStream(os, true, "UTF-8");
		this.nsPrefix = nsPrefix;
		this.nsUri = nsUri;
		init(title);
	}
	
	private void init(String title) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		ps.println("<?xml version=\"1.0\" encoding=\"utf-8\"?>");
		ps.println("<!-- Rules generated on: " + sdf.format(new Date()) + " -->");
		ps.println("<sch:schema xmlns:sch=\"http://www.ascc.net/xml/schematron\">");
		ps.println("	<sch:title>"+title+"</sch:title>");
		ps.println("	<sch:ns prefix=\""+nsPrefix+"\" uri=\""+nsUri+"\"/>");
	}

	private int newRule() {
		return rule_no++;
	}

	public void disallow(String element) {
		newRule("no_"+ element, "Disallowed element: " + element, "No '" + element +"'", nsPrefix + ":" + element, "false");
		/*
		int i = newRule();
		ps.println("	<!-- Rule " + RULE_PREFIX + i +": Disallowed element: " + element + " -->");
		ps.println("	<sch:pattern name=\"no_"+ element +"\" id=\"no_" + element +"\">");
		ps.println("		<sch:rule context=\"" + nsPrefix + ":" + element + "\">");
		ps.println("			<sch:assert test=\"false\">[Rule " + RULE_PREFIX + i + "] No '" + element + "'</sch:assert>");
		ps.println("		</sch:rule>");
		ps.println("	</sch:pattern>");
		*/
	}
	
	public void newRule(String ruleName, String ruleComment, String ruleDesc, String context, String test) {
		int i = newRule();
		ps.println("	<!-- Rule " + RULE_PREFIX + i +": " + ruleComment + " -->");
		ps.println("	<sch:pattern name=\""+ ruleName +"\" id=\"" + ruleName +"\">");
		ps.println("		<sch:rule context=\"" + context + "\">");
		ps.println("			<sch:assert test=\""+test.replaceAll("<", "&lt;")+"\">[Rule " + RULE_PREFIX + i + "] " + ruleDesc + "</sch:assert>");
		ps.println("		</sch:rule>");
		ps.println("	</sch:pattern>");
	}
	
	public void allowIn(String element, String[] allowedElements) {
		int i = newRule();
		ps.print("	<!-- Rule " + RULE_PREFIX + i +": Allow in "+element+" (");
		boolean first = true;
		for (String e2 : allowedElements) {
			if (!first) {
				ps.print(", ");
			} else {
				first = false;
			}
			ps.print(e2);
		}
		ps.println(") --> ");
		ps.println("	<sch:pattern name=\"allow_in_"+element+"\" id=\"allow_in_"+element+"\">");
		ps.println("		<sch:rule context=\"" + nsPrefix + ":" + element + "\">");
		ps.print("			<sch:assert test=\"count(");
		first = true;
		for (String e2 : allowedElements) {
			if (!first) {
				ps.print("|");
			} else {
				first = false;
			}
			ps.print(nsPrefix+":"+e2);
		}
		ps.println(")=count("+nsPrefix+":"+"*)\">[Rule " + RULE_PREFIX + i +"] Unallowed element in \""+element+"\".</sch:assert>");
		ps.println("		</sch:rule>");
		ps.println("	</sch:pattern>");
	}

	public void close() {
		ps.println("</sch:schema>");
	}

}
