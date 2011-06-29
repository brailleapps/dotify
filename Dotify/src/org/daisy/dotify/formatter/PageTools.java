package org.daisy.dotify.formatter;

import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.PageSequence;

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