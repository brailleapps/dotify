package org.daisy.dotify.formatter.impl;

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

	int countPages() {
		int size = 0;
		for (PageSequence ps : this) {
			size += ps.getPageCount();
		}
		return size;
	}

	int countSheets() {
		int sheets = 0;
		for (PageSequence seq : this) {
			LayoutMaster lm = seq.getLayoutMaster();
			if (lm.duplex()) {
				sheets += (int)Math.ceil(seq.getPageCount()/2d);
			} else {
				sheets += seq.getPageCount();
			}
		}
		return sheets;
	}

}