package org.daisy.dotify.formatter;

import org.daisy.dotify.formatter.dom.BookStruct;
import org.daisy.dotify.formatter.dom.VolumeStruct;

/**
 * Provides an interface for organizing a paginated book into 
 * volumes.
 * 
 * @author Joel Håkansson
 */
public interface VolumeSplitter {
	
	/**
	 * Splits the supplied book into volumes.
	 * @param book the book to split
	 * @return the volumes
	 */
	public VolumeStruct split(BookStruct book);

	/**
	 * Sets the target volume size, in other words,
	 * the desired volume size. Depending on implementation,
	 * this value may be a maximum value or it may be
	 * exceeded.
	 * @param sheets the target volume size, in sheets.
	 */
	public void setTargetVolumeSize(int sheets);
}
