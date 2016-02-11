package org.daisy.dotify.devtools.gui.views;

public interface StringFormatterResult {
	
	/**
	 * Sets result to format
	 * @param text text to format
	 */
	public void setResult(String text);
	
	/**
	 * Gets the formatted result as text
	 * @return returns the text 
	 */
	public String getResultAsText();
	
}
