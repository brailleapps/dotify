package org.daisy.dotify.impl.paginator;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.PageStruct;
import org.daisy.dotify.formatter.dom.Row;

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
	
	void newSequence(LayoutMaster master, int pagesOffset, FormatterFactory formatterFactory) {
		this.push(new PageSequenceImpl(master, pagesOffset, this.pageReferences, formatterFactory));
	}
	
	void newSequence(LayoutMaster master, FormatterFactory formatterFactory) {
		if (this.size()==0) {
			newSequence(master, 0, formatterFactory);
		} else {
			int next = currentSequence().currentPage().getPageIndex()+1;
			if (currentSequence().getLayoutMaster().duplex() && (next % 2)==1) {
				next++;
			}
			newSequence(master, next, formatterFactory);
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