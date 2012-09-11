package org.daisy.dotify.config;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.system.ResourceLocatorException;

/**
 * Provides an abstract base for ConfigurationsCatalogs. This can be
 * overridden if a behavior that is different from the default configurations 
 * is desired. In most cases, the default configuration catalog should suffice.
 * @author Joel Håkansson
 */
public abstract class ConfigurationsCatalog {

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
	 * Returns a URL to the configuration specified by the identifier.
	 * @param identifier the configuration key
	 * @return returns a URL to the configuration
	 * @throws ResourceLocatorException if identifier is unknown.
	 */
	public abstract URL getConfigurationURL(String identifier) throws ResourceLocatorException;

}
