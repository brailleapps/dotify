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
	private final int fromIndex;
	private int toIndex;
	
	PageSequence(Stack<PageImpl> pages, LayoutMaster master, int pageOffset) { //, int pageOffset, FormatterFactory formatterFactory) {
		this.pages = pages;
		this.master = master;
		this.pageOffset = pageOffset;
		this.fromIndex = pages.size();
		this.toIndex = pages.size();
	}
	
	void addPage(PageImpl p) {
		pages.add(p);
		toIndex++;
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
		return toIndex-fromIndex;
	}

	public PageImpl getPage(int index) {
		if (index<0 || index>=getPageCount()) {
			throw new ArrayIndexOutOfBoundsException(index);
		}
		return pages.get(index+fromIndex);
	}

	public Iterable<PageImpl> getPages() {
		return pages.subList(fromIndex, toIndex);
	}
	
	int currentPageNumber() {
		return peek().getPageIndex()+1;
	}
	
	public int getPageNumberOffset() {
		return pageOffset;
	}
	
	boolean isSequenceEmpty() {
		return toIndex-fromIndex == 0;
	}
	
	PageImpl peek() {
		return pages.get(toIndex-1);
	}

}
