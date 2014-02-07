package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Marker;

class PageStructImpl extends Stack<PageSequenceImpl> implements PageStruct {
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


	public Iterable<PageSequenceImpl> getContents() {
		return this;
	}

	public PageImpl getPage(String refid) {
		return pageReferences.get(refid);
	}
	
	void newSequence(LayoutMaster master, int pagesOffset) {
		this.push(new PageSequenceImpl(master, pagesOffset, this.pageReferences, context));
	}
	
	void newSequence(LayoutMaster master) {
		if (this.size()==0) {
			newSequence(master, 0);
		} else {
			int next = currentSequence().currentPage().getPageIndex()+1;
			if (currentSequence().getLayoutMaster().duplex() && (next % 2)==1) {
				next++;
			}
			newSequence(master, next);
		}
	}
	
	PageSequenceImpl currentSequence() {
		return this.peek();
	}

	PageImpl currentPage() {
		return currentSequence().currentPage();
	}

	void newPage() {
		currentSequence().newPage();
	}
	
	void newRow(RowImpl row) {
		currentSequence().newRow(row);
	}
	
	void newRow(RowImpl row, String id) {
		currentSequence().newRow(row, id);
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

	int getFlowHeight() {
		return currentPage().getFlowHeight();
	}

}