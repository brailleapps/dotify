package org.daisy.dotify.formatter.utils;

import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.PageSequence;

public class PageTools {

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