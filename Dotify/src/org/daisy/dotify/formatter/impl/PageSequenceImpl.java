package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Page;
import org.daisy.dotify.formatter.PageSequence;
import org.daisy.dotify.formatter.Row;

public class PageSequenceImpl implements Iterable<Page>, PageSequence {
		private Stack<Page> pages;
		private LayoutMaster master;
		private int pagesOffset;
		private final HashMap<String, Integer> pageReferences;
		
		public PageSequenceImpl(LayoutMaster master, int pagesOffset, HashMap<String, Integer> pageReferences) {
			this.pages = new Stack<Page>();
			this.master = master;
			this.pagesOffset = pagesOffset;
			this.pageReferences = pageReferences;
		}

		public int rowsOnCurrentPage() {
			return ((PageImpl)currentPage()).rowsOnPage();
		}
		
		public void newPage() {
			pages.push(new PageImpl(this, pages.size()+pagesOffset));
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
		
		public Page currentPage() {
			return pages.peek();
		}
		
		public void newRow(Row row) {
			if (((PageImpl)currentPage()).rowsOnPage()>=((PageImpl)currentPage()).getFlowHeight()) {
				newPage();
			}
			((PageImpl)currentPage()).newRow(row);
		}
		
		public void newRow(Row row, String id) {
			newRow(row);
			if (pageReferences.put(id, (currentPage().getPageIndex()+1))!=null) {
				throw new IllegalArgumentException("Identifier not unique: " + id);
			}
		}
		
		public LayoutMaster getLayoutMaster() {
			return master;
		}

		public Iterator<Page> iterator() {
			return pages.iterator();
		}

}
