package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Page;
import org.daisy.dotify.formatter.PageSequence;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.formatter.PageTemplate;



/**
 * A page object
 * @author Joel Håkansson, TPB
 */
public class PageImpl implements Page {
	private PageSequence parent;
	private ArrayList<Row> rows;
	private ArrayList<Marker> markers;
	private final int pageIndex;
	private final PageTemplate template;
	private final int flowHeight;
	private int contentMarkersBegin;
	private boolean isVolBreak;
	
	public PageImpl(PageSequence parent, int pageIndex) {
		this.rows = new ArrayList<Row>();
		this.markers = new ArrayList<Marker>();
		this.pageIndex = pageIndex;
		contentMarkersBegin = 0;
		this.parent = parent;
		this.template = parent.getLayoutMaster().getTemplate(pageIndex+1);
		this.flowHeight = parent.getLayoutMaster().getPageHeight()-template.getHeaderHeight()-template.getFooterHeight();
		this.isVolBreak = false;
	}
	
	public void newRow(Row r) {
		if (rowsOnPage()==0) {
			contentMarkersBegin = markers.size();
		}
		rows.add(r);
		markers.addAll(r.getMarkers());
	}
	
	/**
	 * Gets the number of rows on this page
	 * @return returns the number of rows on this page
	 */
	public int rowsOnPage() {
		return rows.size();
	}
	
	public void addMarkers(List<Marker> m) {
		markers.addAll(m);
	}
	
	/**
	 * Get all markers for this page
	 * @return returns a list of all markers on a page
	 */
	public List<Marker> getMarkers() {
		return markers;
	}
	
	/**
	 * Get markers for this page excluding markers before text content
	 * @return returns a list of markers on a page
	 */
	public List<Marker> getContentMarkers() {
		return markers.subList(contentMarkersBegin, markers.size());
	}
	
	public ArrayList<Row> getRows() {
		return rows;
	}

	/**
	 * Get the number for the page
	 * @return returns the page index in the sequence (zero based)
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	public PageSequence getParent() {
		return parent;
	}
	
	/**
	 * Gets the flow height for this page, i.e. the number of rows available for text flow
	 * @return returns the flow height
	 */
	public int getFlowHeight() {
		return flowHeight;
	}

	public boolean isVolumeBreak() {
		return isVolBreak;
	}

	public void setVolumeBreak(boolean value) {
		isVolBreak = value;
	}

}
