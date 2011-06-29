package org.daisy.dotify.formatter.dom;

import java.util.List;

/**
 * A page object
 * @author Joel Håkansson, TPB
 */
public interface Page {
	
	/**
	 * Gets all markers for this page
	 * @return returns a list of all markers on a page
	 */
	public List<Marker> getMarkers();
	
	/**
	 * Gets markers for this page excluding markers before text content
	 * @return returns a list of markers on a page
	 */
	public List<Marker> getContentMarkers();
	
	/**
	 * Gets the rows on this page
	 * @return returns the rows on this page
	 */
	public List<Row> getRows();

	/**
	 * Gets the page index for this page
	 * @return returns the page index in the sequence (zero based)
	 */
	public int getPageIndex();

	/**
	 * Gets the parent sequence for this page
	 * @return returns the parent sequence
	 */
	public PageSequence getParent();

}