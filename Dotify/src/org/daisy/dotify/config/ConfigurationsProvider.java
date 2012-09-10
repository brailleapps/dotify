package org.daisy.dotify.config;

import java.net.URL;
import java.util.Set;

import org.daisy.dotify.system.ResourceLocatorException;

public interface ConfigurationsProvider {
	
	/**
	 * Returns a new configuration manager configured for the specified locale.
	 * @param locale the locale for the new configuration manager
	 * @return returns a new configuration manager
	 */
	public Set<String> getConfigurationKeys();
	
	public URL getConfigurationURL(String identifier) throws ResourceLocatorException;
	
}
