package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.Row;

class PageSequenceImpl implements Iterable<Page>, PageSequence {
		private final Stack<Page> pages;
		private final LayoutMaster master;
		private final int pagesOffset;
		private final HashMap<String, Page> pageReferences;
		private final FormatterFactory formatterFactory;
		private Formatter formatter;
		private int sheetKeep;
		
		PageSequenceImpl(LayoutMaster master, int pagesOffset, HashMap<String, Page> pageReferences, FormatterFactory formatterFactory) {
			this.pages = new Stack<Page>();
			this.master = master;
			this.pagesOffset = pagesOffset;
			this.pageReferences = pageReferences;
			this.formatterFactory = formatterFactory;
			this.formatter = null;
			this.sheetKeep = 0;
		}

		int rowsOnCurrentPage() {
			return ((PageImpl)currentPage()).rowsOnPage();
		}
		
		void newPage() {
			pages.push(new PageImpl(this, pages.size()+pagesOffset));
			if (sheetKeep>0) {
				((PageImpl)currentPage()).setAllowsVolumeBreak(false);
			}
			if (!getLayoutMaster().duplex() || getPageCount()%2==0) {
				if (sheetKeep>0) {
					sheetKeep--;
				}
			}
		}
		
		void setSheetKeepProperty(int value) {
			sheetKeep = Math.max(value, sheetKeep);
			if (sheetKeep>0) {
				((PageImpl)currentPage()).setAllowsVolumeBreak(false);
			}
		}
		
		public int getPageNumberOffset() {
			return pagesOffset;
		}
		
		public int getPageCount() {
			return pages.size();
		}
		
		public Page getPage(int index) {
			return pages.get(index);
		}
		
		Page currentPage() {
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

		public Iterator<Page> iterator() {
			return pages.iterator();
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
