package org.daisy.dotify.formatter.impl;

import java.util.List;

import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Page;
import org.daisy.dotify.formatter.PageSequence;
import org.daisy.dotify.formatter.Row;

public class PageCloneImpl implements Page {
	private final Page p;
	private final PageSequence parent;
	
	public PageCloneImpl(Page p, PageSequence parent) {
		this.p = p;
		this.parent = parent;
	}

	public List<Marker> getMarkers() {
		return p.getMarkers();
	}

	public List<Marker> getContentMarkers() {
		return p.getContentMarkers();
	}

	public List<Row> getRows() {
		return p.getRows();
	}

	public int getPageIndex() {
		return p.getPageIndex();
	}

	public PageSequence getParent() {
		return parent;
	}

}
