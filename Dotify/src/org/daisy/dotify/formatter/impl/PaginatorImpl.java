package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.List;

import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageInfo;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.PageStruct;
import org.daisy.dotify.formatter.dom.Row;
import org.daisy.dotify.tools.StateObject;


public class PaginatorImpl implements Paginator, PageInfo {
	private PageStructImpl pageStruct;
	private StateObject state;
	private FormatterFactory formatterFactory;
	//private HashMap<String, LayoutMaster> templates;

	public PaginatorImpl() { //HashMap<String, LayoutMaster> templates
		
		this.state = new StateObject();
		//this.templates = templates;
	}
	
	public void open(FormatterFactory formatterFactory) {
		state.assertUnopened();
		this.pageStruct = new PageStructImpl();
		this.formatterFactory = formatterFactory;
		state.open();
	}

	public void newSequence(LayoutMaster master, int pagesOffset) {
		state.assertOpen();
		//sequence.push(new PageSequence(templates.get(masterName)));
		pageStruct.push(new PageSequenceImpl(master, pagesOffset, pageStruct.pageReferences, formatterFactory));
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

	public void insertMarkers(List<Marker> m) {
		state.assertOpen();
		((PageImpl)((PageSequenceImpl)currentSequence()).currentPage()).addMarkers(m);
	}
/*
	public LayoutMaster getCurrentLayoutMaster() {
		return currentSequence().getLayoutMaster();
	}*/
	
	public PageInfo getPageInfo() {
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

	public void insertIdentifier(String id) {
		((PageSequenceImpl)currentSequence()).insertIdentifier(id);
	}

}
