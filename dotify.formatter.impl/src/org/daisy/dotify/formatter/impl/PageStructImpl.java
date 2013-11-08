package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.Page;
import org.daisy.dotify.api.formatter.PageSequence;
import org.daisy.dotify.api.formatter.PageStruct;
import org.daisy.dotify.api.formatter.Row;
import org.daisy.dotify.api.translator.BrailleTranslator;

class PageStructImpl extends Stack<PageSequenceImpl> implements PageStruct {
	//private final StringFilter filters;
	HashMap<String, Page> pageReferences;

	
	public PageStructImpl() {
		//this.filters = filters;
		this.pageReferences = new HashMap<String, Page>();
	}
	
	/*public StringFilter getFilter() {
		return filters;
	}*/

	private static final long serialVersionUID = 2591429059130956153L;


	public Iterable<? extends PageSequence> getContents() {
		return this;
	}

	public Page getPage(String refid) {
		return pageReferences.get(refid);
	}
	
	void newSequence(LayoutMaster master, int pagesOffset, BrailleTranslator translator) {
		this.push(new PageSequenceImpl(master, pagesOffset, this.pageReferences, translator));
	}
	
	void newSequence(LayoutMaster master, BrailleTranslator translator) {
		if (this.size()==0) {
			newSequence(master, 0, translator);
		} else {
			int next = currentSequence().currentPage().getPageIndex()+1;
			if (currentSequence().getLayoutMaster().duplex() && (next % 2)==1) {
				next++;
			}
			newSequence(master, next, translator);
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
	
	void newRow(Row row) {
		currentSequence().newRow(row);
	}
	
	void newRow(Row row, String id) {
		currentSequence().newRow(row, id);
	}

	void insertMarkers(List<Marker> m) {
		currentSequence().currentPage().addMarkers(m);
	}
	
	void insertIdentifier(String id) {
		currentSequence().insertIdentifier(id);
	}

	int countRows() {
		return currentPage().rowsOnPage();
	}
	
	int getFlowHeight() {
		return currentPage().getFlowHeight();
	}

}