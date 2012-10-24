package org.daisy.dotify.config;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.tools.ResourceLocatorException;

/**
 * Provides a catalog of configurations available to the input system. The catalog 
 * implementation can be overridden if a different behavior is desired. In most cases, 
 * the default configuration catalog should suffice. To override this class, extend it
 * and add a reference to the implementation to the java service registry.
 * @author Joel Håkansson
 */
public abstract class ConfigurationsCatalog {
	
	protected ConfigurationsCatalog() {}

	/**
	 * Creates a new instance of a configurations catalog. First, the service
	 * registry is scanned for alternate implementations of this class. If none
	 * is found, the default configurations catalog is used. Note that the default
	 * configurations catalog supports multiple configurations providers, which is
	 * the first choice for adding configurations. 
	 * @return returns an instance of a configurations catalog
	 */
	public final static ConfigurationsCatalog newInstance() {
		Iterator<ConfigurationsCatalog> i = ServiceRegistry.lookupProviders(ConfigurationsCatalog.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new DefaultConfigurationsCatalog();
	}
	
	/**
	 * Gets all configuration keys available to the configurations catalog.
	 * @return returns a set of configuration keys.
	 */
	public abstract Set<String> getKeys();
	
	/**
	 * Returns configuration properties associated with the identifier.
	 * @param identifier the configuration key
	 * @return returns properties for the configuration
	 * @throws ResourceLocatorException if identifier is unknown.
	 */
	public abstract Properties getConfiguration(String identifier) throws ResourceLocatorException;
	
	/**
	 * Gets the description for a specified configuration.
	 * @param identifier the configuration key
	 * @return returns the description, or null
	 */
	public abstract String getConfigurationDescription(String identifier);

}
