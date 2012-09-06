package org.daisy.dotify.system;

import java.util.Set;

import org.daisy.dotify.text.FilterLocale;

public interface ConfigurationManagerFactory {
	
	/**
	 * Returns true if this factory can create instances for the specified locale.
	 * @param locale
	 * @return
	 */
	public boolean supportsLocale(FilterLocale locale);
	
	/**
	 * Returns a new configuration manager configured for the specified locale.
	 * @param locale the locale for the new configuration manager
	 * @return returns a new configuration manager
	 */
	public ConfigurationManager newConfigurationManager(FilterLocale locale);
	
	public Set<String> listSupportedLocales();
}
