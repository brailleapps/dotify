package org.daisy.dotify.system.tasks.layout.impl;

import java.io.IOException;
import java.util.ArrayList;

import org.daisy.dotify.system.tasks.layout.flow.Marker;
import org.daisy.dotify.system.tasks.layout.flow.Row;
import org.daisy.dotify.system.tasks.layout.page.CurrentPageInfo;
import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;
import org.daisy.dotify.system.tasks.layout.page.Paginator;
import org.daisy.dotify.system.tasks.layout.text.StringFilter;
import org.daisy.dotify.system.tools.StateObject;


public class PaginatorImpl implements Paginator, CurrentPageInfo {
	private final PageStructImpl pageStruct;
	private StateObject state;
	//private HashMap<String, LayoutMaster> templates;

	public PaginatorImpl(StringFilter filters) { //HashMap<String, LayoutMaster> templates
		this.pageStruct = new PageStructImpl(filters);
		this.state = new StateObject();
		//this.templates = templates;
	}
	
	public void open() {
		state.assertUnopened();
		state.open();
	}

	public void newSequence(LayoutMaster master, int pagesOffset) {
		state.assertOpen();
		//sequence.push(new PageSequence(templates.get(masterName)));
		pageStruct.push(new PageSequenceImpl(master, pagesOffset));
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

	public void insertMarkers(ArrayList<Marker> m) {
		state.assertOpen();
		((PageSequenceImpl)currentSequence()).currentPage().addMarkers(m);
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
		return currentPage().rowsOnPage();
	}
	
	public int getFlowHeight() {
		state.assertOpen();
		return currentPage().getFlowHeight();
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
