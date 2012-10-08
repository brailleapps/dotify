package org.daisy.dotify.formatter.dom.book;

import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;

class PageStructCopy implements Iterable<PageSequence> {
	private final Stack<PageSequence> seq;
	private PageSequence originalSeq;
	private int sheets;
	private int pagesInSeq;
	
	public PageStructCopy() {
		this.seq = new Stack<PageSequence>();
		this.sheets = 0;
		this.pagesInSeq = 0;
	}
	
	public void addPage(Page p) {
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
	
	public int countSheets() {
		return sheets;
	}
	
	/**
	 * Counts the total number of sheets if this page were added
	 * @param p
	 * @return
	 */
	public int countSheets(Page p) {
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