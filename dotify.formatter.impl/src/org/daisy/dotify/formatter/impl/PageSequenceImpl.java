package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.translator.BrailleTranslator;

class PageSequenceImpl implements PageSequence {
		private final Stack<PageImpl> pages;
		private final LayoutMaster master;
		private final int pagesOffset;
		private final HashMap<String, PageImpl> pageReferences;
		private final FormatterContext context;
		private int keepNextSheets;
		private PageImpl nextPage;
		private final List<RowImpl> before;
		private final List<RowImpl> after;
		
		PageSequenceImpl(LayoutMaster master, int pagesOffset, HashMap<String, PageImpl> pageReferences, List<RowImpl> before, List<RowImpl> after, FormatterContext context) {
			this.pages = new Stack<PageImpl>();
			this.master = master;
			this.pagesOffset = pagesOffset;
			this.pageReferences = pageReferences;
			this.context = context;
			this.keepNextSheets = 0;
			this.nextPage = null;
			this.before = before;
			this.after = after;
		}

		int rowsOnCurrentPage() {
			return currentPage().rowsOnPage();
		}
		
		void newPage() {
			if (nextPage!=null) {
				pages.push(nextPage);
				nextPage = null;
			} else {
				pages.push(new PageImpl(master, context, this, pages.size()+pagesOffset, before, after));
			}
			if (keepNextSheets>0) {
				currentPage().setAllowsVolumeBreak(false);
			}
			if (!getLayoutMaster().duplex() || getPageCount()%2==0) {
				if (keepNextSheets>0) {
					keepNextSheets--;
				}
			}
		}
		
		void newPageOnRow() {
			if (nextPage!=null) {
				//if new page is already in buffer, flush it.
				newPage();
			}
			nextPage = new PageImpl(master, context, this, pages.size()+pagesOffset, before, after);
		}
		
		void setKeepWithPreviousSheets(int value) {
			currentPage().setKeepWithPreviousSheets(value);
		}
		
		void setKeepWithNextSheets(int value) {
			keepNextSheets = Math.max(value, keepNextSheets);
			if (keepNextSheets>0) {
				currentPage().setAllowsVolumeBreak(false);
			}
		}
		
		public int getPageNumberOffset() {
			return pagesOffset;
		}
		
		public int getPageCount() {
			return pages.size();
		}
		
		public PageImpl getPage(int index) {
			return pages.get(index);
		}
		
		PageImpl currentPage() {
			if (nextPage!=null) {
				return nextPage;
			} else {
				return pages.peek();
			}
		}

	/**
	 * Space used, in rows
	 * 
	 * @return
	 */
	int spaceUsedOnPage(int offs) {
		return currentPage().spaceUsedOnPage(offs);
	}
	
	void newRow(RowImpl row, List<RowImpl> block) {
		if (nextPage != null || currentPage().spaceNeeded(block) + currentPage().spaceNeeded() + 1 > currentPage().getFlowHeight()) {
			newPage();
		}
		currentPage().newRow(row);
		currentPage().addToPageArea(block);
	}
		
		void newRow(RowImpl row) {
		if (spaceUsedOnPage(1) > currentPage().getFlowHeight() || nextPage != null) {
				newPage();
			}
			currentPage().newRow(row);
		}
			
		public LayoutMaster getLayoutMaster() {
			return master;
		}
/*
		public Iterator<Page> iterator() {
			return (Iterator<Page>)pages.iterator();
		}*/
		
		public Iterable<PageImpl> getPages() {
			return pages;
		}
		
		void insertIdentifier(String id) {
			if (pageReferences.put(id, currentPage())!=null) {
				throw new IllegalArgumentException("Identifier not unique: " + id);
			}
		}
		
		public BrailleTranslator getTranslator() {
			return context.getTranslator();
		}
		
		public FormatterContext getFormatterContext() {
			return context;
		}

}
