package org.daisy.dotify.book;

import org.daisy.dotify.formatter.dom.LayoutMaster;

/**
 * PageTools is a utility class for simple operations related to pages.
 * 
 * @author Joel Håkansson
 */
class PageTools {
	
	// Default constructor is private as this class is not intended to be instantiated.
	private PageTools() { }

	static int countSheets(Iterable<? extends PageSequence> mf) {
		int sheets = 0;
		for (PageSequence seq : mf) {
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