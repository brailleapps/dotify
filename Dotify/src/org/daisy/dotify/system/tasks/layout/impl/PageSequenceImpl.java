package org.daisy.dotify.system.tasks.layout.impl;

import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.system.tasks.layout.flow.Row;
import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;

public class PageSequenceImpl implements Iterable<Page>, PageSequence {
		private Stack<Page> pages;
		private LayoutMaster master;
		private int pagesOffset;
		
		public PageSequenceImpl(LayoutMaster master, int pagesOffset) {
			this.pages = new Stack<Page>();
			this.master = master;
			this.pagesOffset = pagesOffset;
		}

		public int rowsOnCurrentPage() {
			return currentPage().rowsOnPage();
		}
		
		public void newPage() {
			pages.push(new Page(this, pages.size()+pagesOffset));
		}
		
		public int getOffset() {
			return pagesOffset;
		}
		
		public int getSize() {
			return pages.size();
		}
		
		public Page getPage(int index) {
			return pages.get(index);
		}
		
		public Page currentPage() {
			return pages.peek();
		}
		
		public void newRow(Row row) {
			if (currentPage().rowsOnPage()>=currentPage().getFlowHeight()) {
				newPage();
			}
			currentPage().newRow(row);
		}
		
		public LayoutMaster getLayoutMaster() {
			return master;
		}

		public Iterator<Page> iterator() {
			return pages.iterator();
		}

}
