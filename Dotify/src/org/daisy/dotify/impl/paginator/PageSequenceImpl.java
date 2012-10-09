package org.daisy.dotify.impl.paginator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.Row;

class PageSequenceImpl implements PageSequence {
		private final Stack<PageImpl> pages;
		private final LayoutMaster master;
		private final int pagesOffset;
		private final HashMap<String, Page> pageReferences;
		private final FormatterFactory formatterFactory;
		private Formatter formatter;
		private int keepNextSheets;
		
		PageSequenceImpl(LayoutMaster master, int pagesOffset, HashMap<String, Page> pageReferences, FormatterFactory formatterFactory) {
			this.pages = new Stack<PageImpl>();
			this.master = master;
			this.pagesOffset = pagesOffset;
			this.pageReferences = pageReferences;
			this.formatterFactory = formatterFactory;
			this.formatter = null;
			this.keepNextSheets = 0;
		}

		int rowsOnCurrentPage() {
			return ((PageImpl)currentPage()).rowsOnPage();
		}
		
		void newPage() {
			pages.push(new PageImpl(this, pages.size()+pagesOffset));
			if (keepNextSheets>0) {
				((PageImpl)currentPage()).setAllowsVolumeBreak(false);
			}
			if (!getLayoutMaster().duplex() || getPageCount()%2==0) {
				if (keepNextSheets>0) {
					keepNextSheets--;
				}
			}
		}
		
		void setKeepWithPreviousSheets(int value) {
			((PageImpl)currentPage()).setKeepWithPreviousSheets(value);
		}
		
		void setKeepWithNextSheets(int value) {
			keepNextSheets = Math.max(value, keepNextSheets);
			if (keepNextSheets>0) {
				((PageImpl)currentPage()).setAllowsVolumeBreak(false);
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
			return pages.peek();
		}
		
		void newRow(Row row) {
			if (((PageImpl)currentPage()).rowsOnPage()>=((PageImpl)currentPage()).getFlowHeight()) {
				newPage();
			}
			((PageImpl)currentPage()).newRow(row);
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
		
		public FormatterFactory getFormatterFactory() {
			return formatterFactory;
		}

		public Formatter getFormatter() {
			if (formatter == null) {
				formatter = formatterFactory.newFormatter();
			}
			return formatter;
		}

}
