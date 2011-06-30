package org.daisy.dotify.formatter;

import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.PageSequence;

/**
 * PageTools is a utility class for simple operations related to pages.
 * 
 * @author Joel Håkansson
 */
public class PageTools {
	
	// Default constructor is private as this class is not intended to be instantiated.
	private PageTools() { }

	public static int countSheets(Iterable<PageSequence> mf) {
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