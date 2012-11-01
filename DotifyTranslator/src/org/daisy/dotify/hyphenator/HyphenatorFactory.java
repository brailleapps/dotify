package org.daisy.dotify.hyphenator;

import org.daisy.dotify.text.FilterLocale;

public interface HyphenatorFactory {

	/**
	 * Returns true if this instance can create instances for the specified locale.
	 * @param locale
	 * @return returns true if the specified locale is supported, false otherwise
	 */
	public boolean supportsLocale(FilterLocale locale);
	
	/**
	 * Returns a new hyphenator configured for the specified locale.
	 * @param locale the locale for the new hyphenator
	 * @return returns a new hyphenator
	 * @throws UnsupportedLocaleException
	 */
	public HyphenatorInterface newHyphenator(FilterLocale locale) throws UnsupportedLocaleException;
	
	public Object getFeature(String key);
	
	public void setFeature(String key, Object value) throws FeatureNotSupportedException;
}
