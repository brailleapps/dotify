package org.daisy.dotify.formatter;

import org.daisy.dotify.formatter.dom.Volume;
import org.daisy.dotify.formatter.dom.book.BookStruct;

/**
 * Provides an interface for organizing a paginated book into 
 * volumes.
 * 
 * @author Joel Håkansson
 * @deprecated volume splitting should be controlled by OBFL markup, if additional logic is required
 * for volume splitting, modify OBFL to support it.
 */
public interface VolumeSplitter {
	
	/**
	 * Splits the supplied book into volumes.
	 * @param book the book to split
	 * @return the volumes
	 */
	public Iterable<Volume> split(BookStruct book);

}
