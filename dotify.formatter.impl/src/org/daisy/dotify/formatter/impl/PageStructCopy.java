package org.daisy.dotify.formatter.impl;

import java.util.Iterator;
import java.util.List;

import org.daisy.dotify.api.formatter.PageSequence;


class PageStructCopy implements Iterable<PageSequence> {
	private final List<PageSequence> seq;
	private final int size;
	
	public PageStructCopy(List<PageSequence> seq, int size) {
		this.seq = seq;
		this.size = size;
		
	}
	
	int getPageCount() {
		return size;
	}

	public Iterator<PageSequence> iterator() {
		return seq.iterator();
	}

}