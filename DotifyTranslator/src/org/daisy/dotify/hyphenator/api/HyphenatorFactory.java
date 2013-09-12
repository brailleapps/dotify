package org.daisy.dotify.hyphenator.api;

/**
 * Provides a hyphenation factory interface. This interface is used to retreive
 * a hyphenator instance.
 * 
 * @author Joel Håkansson
 *
 */
public interface HyphenatorFactory {

	/**
	 * Returns true if this instance can create instances for the specified
	 * locale.
	 * 
	 * @param locale
	 *            a valid locale as defined by IETF RFC 3066
	 * @return returns true if the specified locale is supported, false
	 *         otherwise
	 */
	public boolean supportsLocale(String locale);
	
	/**
	 * Returns a new hyphenator configured for the specified locale.
	 * 
	 * @param locale
	 *            a valid locale for the new hyphenator, as defined by IETF RFC
	 *            3066
	 * @return returns a new hyphenator
	 * @throws HyphenatorConfigurationException
	 *             if the locale is not supported
	 */
	public HyphenatorInterface newHyphenator(String locale) throws HyphenatorConfigurationException;
	
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
	 * @throws HyphenatorConfigurationException if the feature is not supported
	 */
	public void setFeature(String key, Object value) throws HyphenatorConfigurationException;
}
