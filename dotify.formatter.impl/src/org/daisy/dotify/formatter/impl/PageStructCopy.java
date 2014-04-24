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
		PageSequenceImpl ps;
		int i;
		while (pageIndex<orSeq.getPageCount()) {

			p = orSeq.getPage(pageIndex);
			ps = p.getParent();
			
			//new sheet needed for this page?
			i = 0;
			if (originalSeq != ps || !ps.getLayoutMaster().duplex() || pagesInSeq % 2 == 0) {
				i = 1;
			}
			if (sheets + i<=contentSheets) {
				if (seq.empty() || originalSeq != ps) {
					originalSeq = ps;
					seq.add(new PageSequenceCopy(originalSeq.getLayoutMaster())); //, originalSeq.getPageNumberOffset(), originalSeq.getFormatterFactory()));
					pagesInSeq = 0;
				}
				((PageSequenceCopy)seq.peek()).addPage(p);
				pagesInSeq++;
				if (!ps.getLayoutMaster().duplex() || pagesInSeq % 2 == 1) {
					sheets++;
				}
				pageIndex++;
				size++;
			} else {
				break;
			}
		}
	}
	
	int getPageCount() {
		return size;
	}

	public Iterator<PageSequence> iterator() {
		return seq.iterator();
	}

}