package org.daisy.dotify.formatter.dom;




/**
 * Provides a page oriented structure
 * @author Joel Håkansson
 */
public interface PageStruct {
	
	/**
	 * Gets the contents
	 * @return returns the content
	 */
	public Iterable<PageSequence> getContents();
	public Page getPage(String refid);
	
}