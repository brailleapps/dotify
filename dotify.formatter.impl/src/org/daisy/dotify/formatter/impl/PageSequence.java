package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.LayoutMaster;



/**
 * Provides an interface for a sequence of pages.
 * 
 * @author Joel Håkansson
 */
interface PageSequence {
	
	public Iterable<? extends Page> getPages();
	/**
	 * Gets the number of pages in this sequence
	 * @return returns the number of pages in this sequence
	 */
	public int getPageCount();
	/**
	 * Gets the layout master for this sequence
	 * @return returns the layout master for this sequence
	 */
	public LayoutMaster getLayoutMaster();

}