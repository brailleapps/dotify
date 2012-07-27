package org.daisy.dotify.hyphenator;

import org.daisy.dotify.text.FilterLocale;

public interface HyphenatorInterface {
	
	public boolean supportsLocale(FilterLocale locale);
	/**
	 * Hyphenates the phrase, inserting soft hyphens at all possible breakpoints.
	 * @param phrase
	 * @param locale
	 * @param beginLimit
	 * @param endLimit
	 * @return
	 * @throws UnsupportedLocaleException if the hyphenator does not support the locale
	 */
	public String hyphenate(String phrase, FilterLocale locale, int beginLimit, int endLimit) throws UnsupportedLocaleException;

}
