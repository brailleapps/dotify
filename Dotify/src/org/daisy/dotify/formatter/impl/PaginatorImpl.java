package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;

import org.daisy.dotify.formatter.CurrentPageInfo;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Page;
import org.daisy.dotify.formatter.PageSequence;
import org.daisy.dotify.formatter.PageStruct;
import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.tools.StateObject;


public class PaginatorImpl implements Paginator, CurrentPageInfo {
	private PageStructImpl pageStruct;
	private StateObject state;
	//private HashMap<String, LayoutMaster> templates;

	public PaginatorImpl() { //HashMap<String, LayoutMaster> templates
		
		this.state = new StateObject();
		//this.templates = templates;
	}
	
	public void open() {
		state.assertUnopened();
		this.pageStruct = new PageStructImpl();
		state.open();
	}

	public void newSequence(LayoutMaster master, int pagesOffset) {
		state.assertOpen();
		//sequence.push(new PageSequence(templates.get(masterName)));
		pageStruct.push(new PageSequenceImpl(master, pagesOffset, pageStruct.pageReferences));
	}
	
	public void newSequence(LayoutMaster master) {
		if (pageStruct.size()==0) {
			newSequence(master, 0);
		} else {
			int next = ((PageSequenceImpl)pageStruct.peek()).currentPage().getPageIndex()+1;
			if (pageStruct.peek().getLayoutMaster().duplex() && (next % 2)==1) {
				next++;
			}
			newSequence(master, next);
		}
	}
	
	private PageSequence currentSequence() {
		return pageStruct.peek();
	}

	private Page currentPage() {
		return ((PageSequenceImpl)currentSequence()).currentPage();
	}

	public void newPage() {
		state.assertOpen();
		((PageSequenceImpl)currentSequence()).newPage();
	}
	
	public void newRow(Row row) {
		state.assertOpen();
		((PageSequenceImpl)currentSequence()).newRow(row);
	}
	
	public void newRow(Row row, String id) {
		state.assertOpen();
		((PageSequenceImpl)currentSequence()).newRow(row, id);
	}

	public void insertMarkers(ArrayList<Marker> m) {
		state.assertOpen();
		((PageImpl)((PageSequenceImpl)currentSequence()).currentPage()).addMarkers(m);
	}
/*
	public LayoutMaster getCurrentLayoutMaster() {
		return currentSequence().getLayoutMaster();
	}*/
	
	public CurrentPageInfo getPageInfo() {
		return this;
	}
	
	// CurrentPageInfo
	public int countRows() {
		state.assertOpen();
		//return currentSequence().rowsOnCurrentPage();
		return ((PageImpl)currentPage()).rowsOnPage();
	}
	
	public int getFlowHeight() {
		state.assertOpen();
		return ((PageImpl)currentPage()).getFlowHeight();
	}
	// End CurrentPageInfo

	public PageStruct getPageStruct() {
		state.assertClosed();
		return pageStruct;
	}
	
	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}

}
