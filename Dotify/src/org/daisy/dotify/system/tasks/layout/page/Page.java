package org.daisy.dotify.system.tasks.layout.page;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Row;


/**
 * A page object
 * @author Joel Håkansson, TPB
 */
public class Page {
	private PageSequence parent;
	private ArrayList<Row> rows;
	private ArrayList<Marker> markers;
	private final int pageIndex;
	private final Template template;
	private final int flowHeight;
	private int contentMarkersBegin;
	
	public Page(PageSequence parent, int pageIndex) {
		this.rows = new ArrayList<Row>();
		this.markers = new ArrayList<Marker>();
		this.pageIndex = pageIndex;
		contentMarkersBegin = 0;
		this.parent = parent;
		this.template = parent.getLayoutMaster().getTemplate(pageIndex+1);
		this.flowHeight = parent.getLayoutMaster().getPageHeight()-template.getHeaderHeight()-template.getFooterHeight();
	}
	
	public void newRow(Row r) {
		if (rowsOnPage()==0) {
			contentMarkersBegin = markers.size();
		}
		rows.add(r);
		markers.addAll(r.getMarkers());
	}
	
	public int rowsOnPage() {
		return rows.size();
	}
	
	public void addMarkers(ArrayList<Marker> m) {
		markers.addAll(m);
	}
	
	/**
	 * Get all markers for this page
	 * @return returns a list of all markers on a page
	 */
	public ArrayList<Marker> getMarkers() {
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
	 * Get the flow height
	 * @return returns the flow height, i.e. the height available for the text flow
	 */
	public int getFlowHeight() {
		return flowHeight;
	}

}
