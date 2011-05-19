package org.daisy.dotify.formatter;

import java.util.ArrayList;

import org.daisy.dotify.formatter.FormattingTypes.BreakBefore;
import org.daisy.dotify.formatter.FormattingTypes.Keep;
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
	
}