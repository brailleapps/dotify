package org.daisy.dotify.config;

import java.util.Properties;
import java.util.Set;

import org.daisy.dotify.common.io.ResourceLocatorException;

/**
 * Provides an interface for configurations providers. A configurations
 * provider handles a set of configurations, each accessible via a configuration
 * key.
 * @author Joel Håkansson
 *
 */
public interface ConfigurationsProvider {
	
	/**
	 * Gets all configuration keys available in the provider.
	 * @return returns a set of configuration keys.
	 */
	public Set<String> getConfigurationKeys();
	
	/**
	 * Returns a URL to the configuration specified by the identifier.
	 * @param identifier the configuration key
	 * @return returns a URL to the configuration
	 * @throws ResourceLocatorException if identifier is unknown.
	 */
	//public URL getConfigurationURL(String identifier) throws ResourceLocatorException;
	
	/**
	 * Returns the properties associated with the specified by the identifier.
	 * @param identifier the configuration key
	 * @return returns the configuration properties
	 * @throws ResourceLocatorException if identifier is unknown.
	 */
	public Properties getConfiguration(String identifier) throws ResourceLocatorException;
	
	/**
	 * Gets the description for a specified configuration.
	 * @param identifier the configuration key
	 * @return returns the description, or null
	 */
	public String getConfigurationDescription(String identifier);
	
}
