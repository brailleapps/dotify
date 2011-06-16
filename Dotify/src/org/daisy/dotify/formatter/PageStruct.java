package org.daisy.dotify.formatter;




/**
 * Provides a page oriented structure
 * @author Joel Håkansson
 */
public interface PageStruct extends CrossReferences {
	
	/**
	 * Gets the contents
	 * @return returns the content
	 */
	public Iterable<PageSequence> getContents();

}