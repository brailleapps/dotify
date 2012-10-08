package org.daisy.dotify.formatter.dom;

import java.util.List;


public interface RowDataManager extends Iterable<Row> {

	public List<Marker> getGroupMarkers(); 
	public int getRowCount();
	/**
	 * Returns true if this RowDataManager contains objects that makes the formatting volatile,
	 * i.e. prone to change due to for example cross references.
	 * @return returns true if, and only if, the RowDataManager should be discarded if a new pass is requested,
	 * false otherwise
	 */
	public boolean isVolatile();
	
}
