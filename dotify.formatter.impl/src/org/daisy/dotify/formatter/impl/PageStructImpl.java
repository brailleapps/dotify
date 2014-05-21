package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.Marker;

class PageStructImpl extends Stack<PageSequenceBuilder> implements PageStruct {
	private final static char ZERO_WIDTH_SPACE = '\u200b';
	
	private final FormatterContext context;
	//private final StringFilter filters;
	HashMap<String, PageImpl> pageReferences;
	
	public PageStructImpl(FormatterContext context) {
		//this.filters = filters;
		this.pageReferences = new HashMap<String, PageImpl>();
		this.context = context;
	}
	
	/*public StringFilter getFilter() {
		return filters;
	}*/

	private static final long serialVersionUID = 2591429059130956153L;


	public List<PageSequenceBuilder> getContents() {
		return this;
	}

	public PageImpl getPage(String refid) {
		return pageReferences.get(refid);
	}
	
	void newSequence(LayoutMaster master, int pagesOffset,  List<RowImpl> before, List<RowImpl> after) {
		this.push(new PageSequenceBuilder(master, pagesOffset, this.pageReferences, before, after, context));
	}
	
	void newSequence(LayoutMaster master, List<RowImpl> before, List<RowImpl> after) {
		if (this.size()==0) {
			newSequence(master, 0, before, after);
		} else {
			int next = currentSequence().currentPage().getPageIndex()+1;
			if (currentSequence().getLayoutMaster().duplex() && (next % 2)==1) {
				next++;
			}
			newSequence(master, next, before, after);
		}
	}
	
	PageSequenceBuilder currentSequence() {
		return this.peek();
	}

	private PageImpl currentPage() {
		return currentSequence().currentPage();
	}
	
	/**
	 * Gets the height of the page area, including row spacing.
	 * @return
	 */
	float pageAreaHeight() {
		return currentPage().pageAreaSpaceNeeded();
	}

	void newPage() {
		currentSequence().newPage();
	}
	
	void newRow(RowImpl row, List<RowImpl> block) {
		currentSequence().newRow(row, block);
	}
	
	void newRow(RowImpl row) {
		currentSequence().newRow(row);
	}

	void insertMarkers(List<Marker> m) {
		currentSequence().currentPage().addMarkers(m);
	}
	
	void insertIdentifier(String id) {
		currentSequence().insertIdentifier(id);
	}

	/*	int countRows() {
			return currentPage().rowsOnPage();
		}*/
	
	int spaceUsedInRows(int offs) {
		return currentSequence().spaceUsedOnPage(offs);
	}

	/**
	 * Gets the flow height of the current page.
	 * @return returns the flow height
	 */
	int getFlowHeight() {
		return currentPage().getFlowHeight();
	}
	
	int getPageCount() {
		int size = 0;
		for (PageSequence ps : this) {
			size += ps.getPageCount();
		}
		return size;
	}
	
	PageImpl getPage(int i) {
		for (PageSequenceBuilder ps : this) {
			if (i < ps.getPageCount()) {
				return ps.getPage(i);
			} else {
				i -= ps.getPageCount();
			}
		}
		throw new IndexOutOfBoundsException(i + " is out of bounds." );
	}
	
	/**
	 * Builds a string with possible breakpoints for the contents of this page struct.
	 * Each sheet is represented by a lower case 's' and breakpoints are represented
	 * by zero width space (0x200b).
	 * @return returns a string with allowed breakpoints
	 */
	String buildBreakpointString() {
		StringBuilder res = new StringBuilder();
		boolean volBreakAllowed = true;
		for (PageSequenceBuilder seq : this) {
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
	}
	
	/**
	 * Makes a new sub structure starting from the pageIndex with the specified
	 * number of sheets
	 * @param pageIndex the starting page index
	 * @param contentSheets the number of sheets
	 * @return
	 */
	PageStructCopy substruct(int pageIndex, int contentSheets) {
		Stack<PageSequence> seq = new Stack<PageSequence>();
		int size = 0;
		PageSequence originalSeq = null;
		int sheets = 0;
		int pagesInSeq = 0;
		PageImpl p;
		int offs = 0;
		int i;
		process:for (PageSequenceBuilder ps : this) {
			while (pageIndex-offs < ps.getPageCount()) {
				p = ps.getPage(pageIndex-offs);
				if (pageIndex<getPageCount()) {

					//new sheet needed for this page?
					if (originalSeq != ps || !ps.getLayoutMaster().duplex() || pagesInSeq % 2 == 0) {
						i = 1;
					} else {
						i = 0;
					}
					if (sheets + i<=contentSheets) {
						if (seq.empty() || originalSeq != ps) {
							originalSeq = ps;
							seq.add(new PageSequence(originalSeq.getLayoutMaster())); //, originalSeq.getPageNumberOffset(), originalSeq.getFormatterFactory()));
							pagesInSeq = 0;
						}
						((PageSequence)seq.peek()).addPage(p);
						pagesInSeq++;
						if (!ps.getLayoutMaster().duplex() || pagesInSeq % 2 == 1) {
							sheets++;
						}
						pageIndex++;
						size++;
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
		PageStructCopy body = new PageStructCopy(seq, size);
		return body;
	}

}