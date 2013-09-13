package org.daisy.dotify.paginator.impl;

import java.util.HashMap;
import java.util.Stack;

import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.paginator.Page;
import org.daisy.dotify.paginator.PageSequence;

class PageSequenceImpl implements PageSequence {
		private final Stack<PageImpl> pages;
		private final LayoutMaster master;
		private final int pagesOffset;
		private final HashMap<String, Page> pageReferences;
		private final BrailleTranslator translator;
		private int keepNextSheets;
		private PageImpl nextPage;
		
		PageSequenceImpl(LayoutMaster master, int pagesOffset, HashMap<String, Page> pageReferences, BrailleTranslator translator) {
			this.pages = new Stack<PageImpl>();
			this.master = master;
			this.pagesOffset = pagesOffset;
			this.pageReferences = pageReferences;
			this.translator = translator;
			this.keepNextSheets = 0;
			this.nextPage = null;
		}

		int rowsOnCurrentPage() {
			return currentPage().rowsOnPage();
		}
		
		void newPage() {
			if (nextPage!=null) {
				pages.push(nextPage);
				nextPage = null;
			} else {
				pages.push(new PageImpl(this, pages.size()+pagesOffset));
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
			nextPage = new PageImpl(this, pages.size()+pagesOffset);
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
		
		void newRow(Row row) {
			if (currentPage().rowsOnPage()>=currentPage().getFlowHeight() || nextPage!=null) {
				newPage();
			}
			currentPage().newRow(row);
		}
		
		void newRow(Row row, String id) {
			newRow(row);
			insertIdentifier(id);
		}
		
		public LayoutMaster getLayoutMaster() {
			return master;
		}
/*
		public Iterator<Page> iterator() {
			return (Iterator<Page>)pages.iterator();
		}*/
		
		public Iterable<? extends Page> getPages() {
			return pages;
		}
		
		void insertIdentifier(String id) {
			if (pageReferences.put(id, currentPage())!=null) {
				throw new IllegalArgumentException("Identifier not unique: " + id);
			}
		}
		
		public BrailleTranslator getTranslator() {
			return translator;
		}

}
