package org.daisy.dotify.obfl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.daisy.dotify.api.formatter.Field;
import org.daisy.dotify.api.formatter.PageTemplate;
import org.daisy.dotify.api.obfl.Expression;
import org.daisy.dotify.api.obfl.ExpressionFactory;


class PageTemplateImpl implements PageTemplate {
	private final String condition;
	private final List<List<Field>> header;
	private final List<List<Field>> footer;
	private final HashMap<Integer, Boolean> appliesTo;
	private final ExpressionFactory ef;
	
	public PageTemplateImpl(ExpressionFactory ef) {
		this(null, ef);
	}

	/**
	 * Create a new SimpleTemplate.
	 * @param useWhen string to evaluate. In addition to the syntax of {@link Expression}, the value $page can be
	 * used. This will be replaced by the current page number before the expression is evaluated.
	 */
	public PageTemplateImpl(String useWhen, ExpressionFactory ef) {
		this.condition = useWhen;
		this.header = new ArrayList<List<Field>>();
		this.footer = new ArrayList<List<Field>>();
		this.appliesTo = new HashMap<Integer, Boolean>();
		this.ef = ef;
	}

	public void addToHeader(List<Field> obj) {
		header.add(obj);
	}
	
	public void addToFooter(List<Field> obj) {
		footer.add(obj);
	}

	public List<List<Field>> getHeader() {
		//ArrayList<ArrayList<Object>> ret = new ArrayList<ArrayList<Object>>();
		//ret.add(header);
		return header;
	}
	
	public List<List<Field>> getFooter() {
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
		boolean applies = ef.newExpression().evaluate(condition.replaceAll("\\$page(?=\\W)", "" + pagenum)).equals(true);
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
