package org.daisy.dotify.formatter.impl;

import java.util.List;
import java.util.Stack;

/**
 * Provides a page oriented structure
 * @author Joel Håkansson
 */
class PageStruct extends Stack<PageSequence> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4273679615162808108L;

	int getPageCount() {
		int size = 0;
		for (PageSequence ps : this) {
			size += ps.getPageCount();
		}
		return size;
	}
	
	/**
	 * Gets the contents
	 * @return returns the content
	 */
	public List<PageSequence> getContents() {
		return this;
	}


}