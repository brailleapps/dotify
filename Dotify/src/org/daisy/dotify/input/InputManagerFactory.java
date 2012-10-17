package org.daisy.dotify.input;

import java.util.Set;

import org.daisy.dotify.text.FilterLocale;

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
}
