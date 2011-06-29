package org.daisy.dotify.formatter.dom;

import java.util.ArrayList;
import java.util.HashMap;

import org.daisy.dotify.formatter.Expression;


public class DefaultPageTemplate implements PageTemplate {
	private final String condition;
	private final ArrayList<ArrayList<Object>> header;
	private final ArrayList<ArrayList<Object>> footer;
	private final HashMap<Integer, Boolean> appliesTo;
	
	public DefaultPageTemplate() {
		this(null);
	}

	/**
	 * Create a new SimpleTemplate.
	 * @param useWhen string to evaluate. In addition to the syntax of {@link Expression}, the value $page can be
	 * used. This will be replaced by the current page number before the expression is evaluated.
	 */
	public DefaultPageTemplate(String useWhen) {
		this.condition = useWhen;
		this.header = new ArrayList<ArrayList<Object>>();
		this.footer = new ArrayList<ArrayList<Object>>();
		this.appliesTo = new HashMap<Integer, Boolean>();		
	}

	public void addToHeader(ArrayList<Object> obj) {
		header.add(obj);
	}
	
	public void addToFooter(ArrayList<Object> obj) {
		footer.add(obj);
	}

	public ArrayList<ArrayList<Object>> getHeader() {
		//ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		//ret.add(header);
		return header;
	}
	
	public ArrayList<ArrayList<Object>> getFooter() {
		//ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		//ret.add(footer);
		return footer;
	}

	public boolean appliesTo(int pagenum) {
		if (condition==null) {
			return true;
		}
		// keep a HashMap with calculated results
		if (appliesTo.containsKey(pagenum)) {
			return appliesTo.get(pagenum);
		}
		boolean applies = new Expression().evaluate(condition.replaceAll("\\$page(?=\\W)", ""+pagenum)).equals(true);
		appliesTo.put(pagenum, applies);
		return applies;
	}

	public int getFooterHeight() {
		return footer.size();
	}

	public int getHeaderHeight() {
		return header.size();
	}
}
