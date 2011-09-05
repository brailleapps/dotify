package org.daisy.dotify.formatter.dom;

public interface CrossReferences {
	
	/**
	 * 
	 * @param refid
	 * @return returns the page number, one-based
	 */
	public Page getPage(String refid);

}
