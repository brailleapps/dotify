package org.daisy.dotify.book;

import java.util.List;


/**
 * Provides a method for creating a shallow copy of a page. 
 * The copy can have another parent than the original page.
 *   
 * @author Joel Håkansson
 */
class PageCopy implements Page {
	private final Page p;
	private final PageSequence parent;
	
	PageCopy(Page p, PageSequence parent) {
		this.p = p;
		this.parent = parent;
	}
/*
	public List<Marker> getMarkers() {
		return p.getMarkers();
	}

	public List<Marker> getContentMarkers() {
		return p.getContentMarkers();
	}
*/
	public List<Row> getRows() {
		return p.getRows();
	}

	public int getPageIndex() {
		return p.getPageIndex();
	}

	public PageSequence getParent() {
		return parent;
	}

	public boolean allowsVolumeBreak() {
		return p.allowsVolumeBreak();
	}

	public int keepPreviousSheets() {
		return p.keepPreviousSheets();
	}

}
