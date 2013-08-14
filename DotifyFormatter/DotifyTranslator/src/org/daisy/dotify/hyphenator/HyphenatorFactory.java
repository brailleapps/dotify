package org.daisy.dotify.hyphenator;

import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a hyphenation factory interface. This interface is used to retreive
 * a hyphenator instance.
 * 
 * @author Joel Håkansson
 *
 */
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
	 * @throws UnsupportedLocaleException if the locale is not supported
	 */
	public HyphenatorInterface newHyphenator(FilterLocale locale) throws UnsupportedLocaleException;
	
	/**
	 * Gets the value of a hyphenation feature.
	 * @param key the feature to get the value for
	 * @return returns the value, or null if not set
	 */
	public Object getFeature(String key);
	
	/**
	 * Sets the value of a hyphenation feature.
	 * @param key the feature to set the value for
	 * @param value the value for the feature
	 * @throws UnsupportedFeatureException if the feature is not supported
	 */
	public void setFeature(String key, Object value) throws UnsupportedFeatureException;
}
