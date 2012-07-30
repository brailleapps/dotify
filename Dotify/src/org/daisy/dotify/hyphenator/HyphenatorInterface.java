package org.daisy.dotify.hyphenator;

import org.daisy.dotify.text.FilterLocale;

public interface HyphenatorInterface {
	
	public boolean supportsLocale(FilterLocale locale);
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
	
	/**
	 * Returns a new hyphenator configured for the specified locale.
	 * @param locale the locale for the new hyphenator
	 * @return returns a new hyphenator
	 * @throws UnsupportedLocaleException
	 */
	public HyphenatorInterface newHyphenator(FilterLocale locale) throws UnsupportedLocaleException;
}
