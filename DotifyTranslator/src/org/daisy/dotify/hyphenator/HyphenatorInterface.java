package org.daisy.dotify.hyphenator;


public interface HyphenatorInterface {
	
	/**
	 * Hyphenates the phrase, inserting soft hyphens at all possible breakpoints.
	 * @param phrase
	 */
	public String hyphenate(String phrase);

	public int getBeginLimit();
	
	/**
	 * Sets the earliest position in a word where a break point may be inserted.
	 * @param beginLimit
	 */
	public void setBeginLimit(int beginLimit);
	
	public int getEndLimit();
	public void setEndLimit(int endLimit);
	
}
