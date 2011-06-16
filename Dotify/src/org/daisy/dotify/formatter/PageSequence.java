package org.daisy.dotify.formatter;

public interface PageSequence extends Iterable<Page>, Sequence {
	/**
	 * Gets the number of pages in this sequence
	 * @return returns the number of pages in this sequence
	 */
	public int getPageCount();
	/**
	 * Gets the page with the specified index, where index >= 0 && index < getPageCount()
	 * @param index the page index
	 * @return returns the page index
	 * @throws IndexOutOfBoundsException if index < 0 || index >= getPageCount()
	 */
	public Page getPage(int index);
	/**
	 * Gets the page number offset for this page sequence
	 * @return returns the page number offset
	 */
	public int getPageNumberOffset();
}