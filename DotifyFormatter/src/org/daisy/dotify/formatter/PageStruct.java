package org.daisy.dotify.formatter;





/**
 * Provides a page oriented structure
 * @author Joel Håkansson
 */
public interface PageStruct {
	
	/**
	 * Gets the contents
	 * @return returns the content
	 */
	public Iterable<? extends PageSequence> getContents();
	public Page getPage(String refid);
	
}