package org.daisy.dotify.formatter.dom;

import java.util.List;

public interface RowDataManager extends Iterable<Row> {

	public List<Marker> getGroupMarkers(); 
	public int getRowCount();
	public boolean isDirty();
	
}
