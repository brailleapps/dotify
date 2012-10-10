package org.daisy.dotify.formatter;

import org.daisy.dotify.book.Page;

/**
 * Provides an interface for cross references, that is to say
 * the location of a specific identifier.
 * @author Joel Håkansson
 *
 */
public interface CrossReferences {
	
	/**
	 * Gets the page for the specified identifier.
	 * @param refid the identifier to get the page for
	 * @return returns the page number, one-based
	 */
	public Page getPage(String refid);
	
	/**
	 * Gets the volume for the specified identifier.
	 * @param refid the identifier to get the volume for
	 * @return returns the volume number, one-based
	 */
	public Integer getVolumeNumber(String refid);

}
