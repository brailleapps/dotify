package org.daisy.dotify.text;

import org.daisy.dotify.hyphenator.UnsupportedFeatureException;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;

/**
 * Provides a integer2text factory interface. This interface is used to retreive
 * a integer2text instance.
 * 
 * @author Joel Håkansson
 * 
 */
public interface Integer2TextFactory {

	/**
	 * Returns true if this instance can create instances for the specified locale.
	 * @param locale
	 * @return returns true if the specified locale is supported, false otherwise
	 */
	public boolean supportsLocale(FilterLocale locale);
	
	/**
	 * Returns a new integer2text configured for the specified locale.
	 * 
	 * @param locale
	 *            the locale for the new integer2text
	 * @return returns a new integer2text
	 * @throws UnsupportedLocaleException
	 *             if the locale is not supported
	 */
	public Integer2Text newInteger2Text(FilterLocale locale) throws UnsupportedLocaleException;
	
	/**
	 * Gets the value of a integer2text feature.
	 * 
	 * @param key
	 *            the feature to get the value for
	 * @return returns the value, or null if not set
	 */
	public Object getFeature(String key);
	
	/**
	 * Sets the value of a integer2text feature.
	 * 
	 * @param key
	 *            the feature to set the value for
	 * @param value
	 *            the value for the feature
	 * @throws UnsupportedFeatureException
	 *             if the feature is not supported
	 */
	public void setFeature(String key, Object value) throws UnsupportedFeatureException;
}
