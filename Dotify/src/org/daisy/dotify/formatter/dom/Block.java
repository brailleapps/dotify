package org.daisy.dotify.formatter.dom;

import java.util.ArrayList;

import org.daisy.dotify.formatter.dom.FormattingTypes.BreakBefore;
import org.daisy.dotify.formatter.dom.FormattingTypes.Keep;
/**
 * Provides a block of rows and the properties
 * associated with it.
 * @author Joel Håkansson
 */
public interface Block extends Iterable<Row> {

	/**
	 * Gets the number of empty rows that should precede the 
	 * rows in this block.
	 * @return returns the number of empty rows preceding the rows in this block
	 */
	public int getSpaceBefore();
	public int getSpaceAfter();
	public int getKeepWithNext();
	public String getIdentifier();
	public Keep getKeepType();
	public ArrayList<Marker> getGroupMarkers();
	public BreakBefore getBreakBeforeType();
	public int getRowCount();
	public String getBlockIdentifier();
	
}