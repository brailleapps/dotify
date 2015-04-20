package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FieldList;
import org.daisy.dotify.api.formatter.PageTemplateBuilder;
import org.daisy.dotify.api.obfl.Expression;


/**
 * Specifies page objects such as header and footer
 * for the pages to which it applies.
 * @author Joel Håkansson
 */
class PageTemplate implements PageTemplateBuilder {
	private final Condition condition;
	private final List<FieldList> header;
	private final List<FieldList> footer;
	private final HashMap<Integer, Boolean> appliesTo;
	
	PageTemplate() {
		this(null);
	}

	/**
	 * Create a new SimpleTemplate.
	 * @param useWhen string to evaluate. In addition to the syntax of {@link Expression}, the value $page can be
	 * used. This will be replaced by the current page number before the expression is evaluated.
	 */
	PageTemplate(Condition condition) {
		this.condition = condition;
		this.header = new ArrayList<FieldList>();
		this.footer = new ArrayList<FieldList>();
		this.appliesTo = new HashMap<Integer, Boolean>();
	}

	public void addToHeader(FieldList obj) {
		header.add(obj);
	}
	
	public void addToFooter(FieldList obj) {
		footer.add(obj);
	}


	/**
	 * Gets header rows for a page using this Template. Each FieldList must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each FieldList must
	 * be smaller than the flow width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @return returns a List of FieldList
	 */
	List<FieldList> getHeader() {
		//ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		//ret.add(header);
		return header;
	}
	
	/**
	 * Gets footer rows for a page using this Template. Each FieldList must 
	 * fit within a single row, i.e. the combined length of all resolved strings in each FieldList must
	 * be smaller than the flow width. Keep in mind that text filters will be applied to the 
	 * resolved string, which could affect its length.
	 * @return returns a List of FieldList
	 */
	List<FieldList> getFooter() {
		//ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		//ret.add(footer);
		return footer;
	}

	/**
	 * Tests if this Template applies to this pagenum.
	 * @param pagenum the pagenum to test
	 * @return returns true if the Template should be applied to the page
	 */
	boolean appliesTo(int pagenum) {
		if (condition==null) {
			return true;
		}
		// keep a HashMap with calculated results
		if (appliesTo.containsKey(pagenum)) {
			return appliesTo.get(pagenum);
		}
		boolean applies = condition.evaluate(new PageContext(pagenum));
		appliesTo.put(pagenum, applies);
		return applies;
	}
	
	private class PageContext implements Context {
		private final Integer page;
		
		private PageContext(int pagenum) {
			this.page = pagenum;
		}

		public Integer getCurrentVolume() {
			return null;
		}

		public Integer getVolumeCount() {
			return null;
		}

		public Integer getCurrentPage() {
			return page;
		}

		public Integer getMetaVolume() {
			return null;
		}

		public Integer getMetaPage() {
			return null;
		}
		
	}

}
