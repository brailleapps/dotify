package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FieldList;
import org.daisy.dotify.api.formatter.PageTemplateBuilder;
import org.daisy.dotify.api.obfl.Expression;


class PageTemplateImpl implements PageTemplate, PageTemplateBuilder {
	private final Condition condition;
	private final List<FieldList> header;
	private final List<FieldList> footer;
	private final HashMap<Integer, Boolean> appliesTo;
	
	public PageTemplateImpl() {
		this(null);
	}

	/**
	 * Create a new SimpleTemplate.
	 * @param useWhen string to evaluate. In addition to the syntax of {@link Expression}, the value $page can be
	 * used. This will be replaced by the current page number before the expression is evaluated.
	 */
	public PageTemplateImpl(Condition condition) {
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

	public List<FieldList> getHeader() {
		//ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		//ret.add(header);
		return header;
	}
	
	public List<FieldList> getFooter() {
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
