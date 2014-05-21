package org.daisy.dotify.formatter.impl;

import java.util.Stack;

/**
 * Provides a method for creating a shallow copy of a PageSequence. 
 * 
 * @author Joel Håkansson
 *
 */
class PageSequenceCopy implements PageSequence {
	private final Stack<Page> pages;
	private final LayoutMaster master;
	
	PageSequenceCopy(LayoutMaster master) { //, int pageOffset, FormatterFactory formatterFactory) {
		this.pages = new Stack<Page>();
		this.master = master;
	}
	
	void addPage(PageImpl p) {
		pages.add(p);
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

	public Iterable<? extends Page> getPages() {
		return pages;
	}

}
