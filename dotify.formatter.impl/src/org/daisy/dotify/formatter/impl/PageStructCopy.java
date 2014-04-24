package org.daisy.dotify.formatter.impl;

import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.api.formatter.PageSequence;


class PageStructCopy implements Iterable<PageSequence> {
	private final Stack<PageSequence> seq;
	private PageSequence originalSeq;
	private int sheets;
	private int pagesInSeq;
	private int size;
	
	public PageStructCopy(PageStructImpl orSeq, int pageIndex, int contentSheets) {
		this.seq = new Stack<PageSequence>();
		this.sheets = 0;
		this.pagesInSeq = 0;
		this.size = 0;
		PageImpl p;
		while (pageIndex<orSeq.getPageCount() && countSheets(p = orSeq.getPage(pageIndex))<=contentSheets) {
			addPage(p);
			pageIndex++;
			size++;
		}
	}
	
	private void addPage(PageImpl p) {
		if (seq.empty() || originalSeq != p.getParent()) {
			originalSeq = p.getParent();
			seq.add(new PageSequenceCopy(originalSeq.getLayoutMaster())); //, originalSeq.getPageNumberOffset(), originalSeq.getFormatterFactory()));
			pagesInSeq = 0;
		}
		((PageSequenceCopy)seq.peek()).addPage(p);
		pagesInSeq++;
		if (!p.getParent().getLayoutMaster().duplex() || pagesInSeq % 2 == 1) {
			sheets++;
		}
	}

	int getPageCount() {
		return size;
	}
	
	/**
	 * Counts the total number of sheets if this page were added
	 * @param p
	 * @return
	 */
	private int countSheets(PageImpl p) {
		int i = 0;
		if (originalSeq != p.getParent() || !p.getParent().getLayoutMaster().duplex() || pagesInSeq % 2 == 0) {
			i = 1;
		}
		return sheets + i;
	}

	public Iterator<PageSequence> iterator() {
		return seq.iterator();
	}

}