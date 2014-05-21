package org.daisy.dotify.formatter.impl;

import java.util.Stack;


class PageStructCopy extends Stack<PageSequence> {

	int getPageCount() {
		int size = 0;
		for (PageSequence ps : this) {
			size += ps.getPageCount();
		}
		return size;
	}
}