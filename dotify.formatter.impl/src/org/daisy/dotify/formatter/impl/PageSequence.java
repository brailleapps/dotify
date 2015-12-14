package org.daisy.dotify.formatter.impl;

import java.util.Stack;

/**
 * Provides a sequence of pages.
 * 
 * @author Joel Håkansson
 */
class PageSequence {
	private final Stack<PageImpl> pages;
	private final LayoutMaster master;
	private final int pageOffset;
	
	PageSequence(LayoutMaster master, int pageOffset) { //, int pageOffset, FormatterFactory formatterFactory) {
		this.pages = new Stack<>();
		this.master = master;
		this.pageOffset = pageOffset;
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
	
	public int getPageNumberOffset() {
		return pageOffset;
	}
	
	boolean isSequenceEmpty() {
		return pages.isEmpty();
	}
	
	PageImpl peek() {
		return pages.peek();
	}

}
