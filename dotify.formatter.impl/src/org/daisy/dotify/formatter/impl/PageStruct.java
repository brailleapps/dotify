package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Stack;

/**
 * Provides a page oriented structure
 * @author Joel Håkansson
 */
class PageStruct implements Iterable<PageSequence> {
	//private final static char ZERO_WIDTH_SPACE = '\u200b';
	private final Stack<PageSequence> seqs;
	private final Stack<PageImpl> pages;
	private final Map<Integer, PageView> volumeViews;
	private int sheetCount;
	
	PageStruct() {
		seqs = new Stack<>();
		pages = new Stack<>();
		volumeViews = new HashMap<>();
		sheetCount = 0;
	}
	
	void updateSheetCount() {
		sheetCount = countSheets(this);
	}
	
	int getSheetCount() {
		return sheetCount;
	}

	static int countPages(Iterable<PageSequence> seqs) {
		int size = 0;
		for (PageSequence ps : seqs) {
			size += ps.getPageCount();
		}
		return size;
	}

	static int countSheets(Iterable<PageSequence> seqs) {
		int sheets = 0;
		for (PageSequence seq : seqs) {
			LayoutMaster lm = seq.getLayoutMaster();
			if (lm.duplex()) {
				sheets += (int)Math.ceil(seq.getPageCount()/2d);
			} else {
				sheets += seq.getPageCount();
			}
		}
		return sheets;
	}
	
	/**
	 * Builds a string with possible breakpoints for the contents of this page struct.
	 * Each sheet is represented by a lower case 's' and breakpoints are represented
	 * by zero width space (0x200b).
	 * @return returns a string with allowed breakpoints
	 */
/*
	String buildBreakpointString() {
		StringBuilder res = new StringBuilder();
		boolean volBreakAllowed = true;
		for (PageSequence seq : seqs) {
			StringBuilder sb = new StringBuilder();
			LayoutMaster lm = seq.getLayoutMaster();
			int pageIndex=0;
			for (PageImpl p : seq.getPages()) {
				if (!lm.duplex() || pageIndex%2==0) {
					volBreakAllowed = true;
					sb.append("s");
				}
				volBreakAllowed &= p.allowsVolumeBreak();
				trimEnd(sb, p);
				if (!lm.duplex() || pageIndex%2==1) {
					if (volBreakAllowed) {
						sb.append(ZERO_WIDTH_SPACE);
					}
				}
				pageIndex++;
			}
			res.append(sb);
			res.append(ZERO_WIDTH_SPACE);
		}
		return res.toString();
	}
*/
	
	List<Sheet> buildSplitPoints() {
		List<Sheet.Builder> ret = new ArrayList<>();
		boolean volBreakAllowed = true;
		int size = 0;
		for (PageSequence seq : seqs) {
			LayoutMaster lm = seq.getLayoutMaster();
			int pageIndex=0;
			Sheet.Builder s = null;
			for (PageImpl p : seq.getPages()) {
				if (!lm.duplex() || pageIndex%2==0) {
					volBreakAllowed = true;
					s = new Sheet.Builder();
					ret.add(s);
				}
				setPreviousSheet(ret, p);
				volBreakAllowed &= p.allowsVolumeBreak();
				if (!lm.duplex() || pageIndex%2==1) {
					if (volBreakAllowed) {
						s.breakable(true);
					}
				}
				s.add(p);
				pageIndex++;
			}
			//if this sequence was not empty
			if (size<ret.size()) {
				size += ret.size();
				//set breakable to true
				ret.get(ret.size()-1).breakable(true);
			}
		}
		return buildAll(ret);
	}
	static String toString(List<Sheet> units) {
		StringBuilder debug = new StringBuilder();
		for (Sheet s : units) {
			debug.append("s");
			if (s.isBreakable()) {
				debug.append("-");
			}
		}
		return debug.toString();
	}
	private List<Sheet> buildAll(List<Sheet.Builder> builders) {
		List<Sheet> ret = new ArrayList<>();
		for (Sheet.Builder b : builders) {
			ret.add(b.build());
		}
		return ret;
	}
/*
	private void trimEnd(StringBuilder sb, PageImpl p) {
		int i = 0;
		int x = sb.length()-1;
		while (i<p.keepPreviousSheets() && x>0) {
			if (sb.charAt(x)=='s') {
				x--;
				i++;
			}
			if (sb.charAt(x)==ZERO_WIDTH_SPACE) {
				sb.deleteCharAt(x);
				x--;
			}
		}
	}*/

	private void setPreviousSheet(List<Sheet.Builder> sb, PageImpl p) {
		int i = 0;
		for (int x = sb.size()-1; i<p.keepPreviousSheets() && x>0; x--) {
			sb.get(x-1).breakable(false);
			i++;
		}
	}
	
	boolean add(PageSequence seq) {
		return seqs.add(seq);
	}
	
	boolean empty() {
		return seqs.empty();
	}
	
	PageSequence peek() {
		return seqs.peek();
	}
	
	int size() {
		return seqs.size();
	}
	
	Stack<PageImpl> getPages() {
		return pages;
	}
	
	PageView getPageView() {
		return new PageView(pages, 0, pages.size());
	}
	
	PageView getContentsInVolume(int volumeNumber) {
		return volumeViews.get(volumeNumber);
	}
	
	/**
	 * Makes a new sub structure starting from the pageIndex with the specified
	 * number of sheets
	 * @param pageIndex the starting page index
	 * @param contentSheets the number of sheets
	 * @return
	 */
	Iterable<PageSequence> substruct(int pageIndex, int contentSheets) {
		PageStruct body = new PageStruct();
		PageSequence originalSeq = null;
		int sheets = 0;
		int pagesInSeq = 0;
		PageImpl p;
		int offs = 0;
		int i;
		process:for (PageSequence ps : seqs) {
			while (pageIndex-offs < ps.getPageCount()) {
				p = ps.getPage(pageIndex-offs);
				if (pageIndex<countPages(this)) {

					//new sheet needed for this page?
					if (originalSeq != ps || !ps.getLayoutMaster().duplex() || pagesInSeq % 2 == 0) {
						i = 1;
					} else {
						i = 0;
					}
					if (sheets + i<=contentSheets) {
						if (body.empty() || originalSeq != ps) {
							originalSeq = ps;
							body.add(new PageSequence(body, originalSeq.getLayoutMaster(), originalSeq.getPageNumberOffset())); //, originalSeq.getPageNumberOffset(), originalSeq.getFormatterFactory()));
							pagesInSeq = 0;
						}
						((PageSequence)body.peek()).addPage(p);
						pagesInSeq++;
						if (!ps.getLayoutMaster().duplex() || pagesInSeq % 2 == 1) {
							sheets++;
						}
						pageIndex++;
					} else {
						//we have what we need
						break process;
					}
				} else {
					//no more content
					break process;
				}
			}
			offs += ps.getPageCount();
		}
		return body;
	}
	
	void setVolumeScope(int volumeNumber, int fromIndex, int toIndex) {
		PageView pw = new PageView(pages, fromIndex, toIndex);
		for (PageImpl p : pw.getPages()) {
			p.setVolumeNumber(volumeNumber);
		}
		volumeViews.put(volumeNumber, pw);
	}

	@Override
	public Iterator<PageSequence> iterator() {
		return seqs.iterator();
	}


}