package org.daisy.dotify.book;

import org.daisy.dotify.paginator.PageSequence;


/**
 * Provides a container for a physical volume of braille
 * @author Joel Håkansson
 */
public interface Volume {

	/**
	 * Gets the contents
	 * @return returns the contents
	 */
	public Iterable<? extends PageSequence> getContents();
	
}
