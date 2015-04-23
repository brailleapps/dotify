package org.daisy.dotify.input;

import java.util.Set;

import org.daisy.dotify.common.text.FilterLocale;

/**
 * Provides an interface for input manager factories. An input manager
 * factory implementation provides input managers for a any number of 
 * supported specifications.
 * 
 * @author Joel Håkansson
 */
public interface InputManagerFactory {
	
	/**
	 * Returns true if this factory can create instances for the specified locale.
	 * @param locale the locale to test
	 * @return true if this factory can create instances for the specified locale, false otherwise
	 */
	public boolean supportsSpecification(FilterLocale locale, String fileFormat);
	
	/**
	 * Returns a new input manager configured for the specified locale.
	 * @param locale the locale for the new input manager
	 * @return returns a new input manager
	 */
	public InputManager newInputManager(FilterLocale locale, String fileFormat);
	
	/**
	 * Lists the supported locales.
	 * @return returns a set of supported locales
	 */
	public Set<String> listSupportedLocales();

	/**
	 * Lists the supported file formats.
	 * @return returns a set of supported formats
	 */
	public Set<String> listSupportedFileFormats();
}
