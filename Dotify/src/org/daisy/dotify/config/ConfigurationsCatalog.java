package org.daisy.dotify.config;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Logger;

import org.daisy.dotify.common.io.ResourceLocatorException;

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
		Iterator<ConfigurationsCatalog> i = ServiceLoader.load(ConfigurationsCatalog.class).iterator();
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

	
	/**
	 * Provides a default configurations catalog. The default configurations catalog
	 * will scan the service registry for configurations providers and collect the keys
	 * of every provider. If more than one provider contains the same key, the 
	 * most recently added will be used, and a debug message will be sent to the log.
	 * @author Joel Håkansson
	 */
	private static class DefaultConfigurationsCatalog extends ConfigurationsCatalog {
		private final Map<String, ConfigurationsProvider> map;
		
		DefaultConfigurationsCatalog() {
			super();
			this.map = new HashMap<String, ConfigurationsProvider>();
			Iterator<ConfigurationsProvider> i = ServiceLoader.load(ConfigurationsProvider.class).iterator();
			Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
			while (i.hasNext()) {
				ConfigurationsProvider f = i.next();
				for (String key : f.getConfigurationKeys()) {
					Object o = map.put(key, f);
					if (o!=null) {
						logger.fine("Key " + key + " in " + o.getClass().getCanonicalName() + " replaced by key in " + f.getClass().getCanonicalName());
					}
				}
			}
		}
		
		@Override
		public Set<String> getKeys() {
			return map.keySet();
		}

		@Override
		public Properties getConfiguration(String identifier) throws ResourceLocatorException {
			ConfigurationsProvider provider = map.get(identifier);
			if (provider!=null) {
				return provider.getConfiguration(identifier);
			} else {
				throw new ResourceLocatorException("Failed to locate resource with identifier: " + identifier);
			}
		}

		@Override
		public String getConfigurationDescription(String identifier) {
			ConfigurationsProvider provider = map.get(identifier);
			if (provider!=null) {
				return provider.getConfigurationDescription(identifier);
			} else {
				return "";
			}
		}
	}
}
