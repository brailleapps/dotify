package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.PageSequence;





/**
 * Provides a page oriented structure
 * @author Joel Håkansson
 */
interface PageStruct {

	/**
	 * Gets the contents
	 * @return returns the content
	 */
	public Iterable<? extends PageSequence> getContents();

}