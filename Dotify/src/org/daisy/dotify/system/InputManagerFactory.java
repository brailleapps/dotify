package org.daisy.dotify.system;

import java.util.Set;

import org.daisy.dotify.text.FilterLocale;

public interface InputManagerFactory {
	
	/**
	 * Returns true if this factory can create instances for the specified locale.
	 * @param locale
	 * @return
	 */
	public boolean supportsLocale(FilterLocale locale);
	
	/**
	 * Returns a new input manager configured for the specified locale.
	 * @param locale the locale for the new input manager
	 * @return returns a new input manager
	 */
	public InputManager newInputManager(FilterLocale locale);
	
	public Set<String> listSupportedLocales();
}
