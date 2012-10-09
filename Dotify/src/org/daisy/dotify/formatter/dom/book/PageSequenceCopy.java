package org.daisy.dotify.formatter.dom.book;

import java.util.Stack;

import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;

/**
 * Provides a method for creating a shallow copy of a PageSequence. 
 * 
 * @author Joel Håkansson
 *
 */
class PageSequenceCopy implements PageSequence {
	private final Stack<Page> pages;
	private final LayoutMaster master;
	//private final int pageOffset;
	//private final FormatterFactory formatterFactory;
	//private Formatter formatter;
	
	PageSequenceCopy(LayoutMaster master) { //, int pageOffset, FormatterFactory formatterFactory) {
		this.pages = new Stack<Page>();
		this.master = master;
		//this.pageOffset = pageOffset;
		//this.formatterFactory = formatterFactory;
		//this.formatter = null;
	}
	
	void addPage(Page p) {
		pages.add(new PageCopy(p, this));
	}

	public LayoutMaster getLayoutMaster() {
		return master;
	}

	public int getPageCount() {
		return pages.size();
	}

	public Page getPage(int index) {
		return pages.get(index);
	}
/*
	public int getPageNumberOffset() {
		return pageOffset;
	}

	public FormatterFactory getFormatterFactory() {
		return formatterFactory;
	}

	public Formatter getFormatter() {
		if (formatter == null) {
			formatter = formatterFactory.newFormatter();
		}
		return formatter;
	}
*/
	public Iterable<? extends Page> getPages() {
		return pages;
	}

}
