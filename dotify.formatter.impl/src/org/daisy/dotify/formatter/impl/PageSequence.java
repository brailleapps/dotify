package org.daisy.dotify.formatter.impl;

import java.util.Stack;

/**
 * Provides a sequence of pages.
 * 
 * @author Joel Håkansson
 */
class PageSequence {
	protected final Stack<PageImpl> pages;
	protected final LayoutMaster master;
	
	PageSequence(LayoutMaster master) { //, int pageOffset, FormatterFactory formatterFactory) {
		this.pages = new Stack<PageImpl>();
		this.master = master;
	}
	
	void addPage(PageImpl p) {
		pages.add(p);
	}

	/**
	 * Gets the layout master for this sequence
	 * @return returns the layout master for this sequence
	 */
	public LayoutMaster getLayoutMaster() {
		return master;
	}

	/**
	 * Gets the number of pages in this sequence
	 * @return returns the number of pages in this sequence
	 */
	public int getPageCount() {
		return pages.size();
	}

	public PageImpl getPage(int index) {
		return pages.get(index);
	}

	public Iterable<PageImpl> getPages() {
		return pages;
	}

}
