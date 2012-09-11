package org.daisy.dotify.config;

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.system.ResourceLocatorException;

/**
 * Provides a default configurations catalog. The default configurations catalog
 * will scan the service registry for configurations providers and collect the keys
 * of every provider. If more than one provider contains the same key, the 
 * most recently added will be used, and a debug message will be sent to the log.
 * @author Joel Håkansson
 */
class DefaultConfigurationsCatalog extends ConfigurationsCatalog {
	private final Map<String, ConfigurationsProvider> map;
	
	DefaultConfigurationsCatalog() {
		super();
		this.map = new HashMap<String, ConfigurationsProvider>();
		Iterator<ConfigurationsProvider> i = ServiceRegistry.lookupProviders(ConfigurationsProvider.class);
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
	
	public Set<String> getKeys() {
		return map.keySet();
	}

	public URL getConfigurationURL(String identifier) throws ResourceLocatorException {
		ConfigurationsProvider provider = map.get(identifier);
		if (provider!=null) {
			return provider.getConfigurationURL(identifier);
		} else {
			throw new ResourceLocatorException("Failed to locate resource with identifier: " + identifier);
		}
	}

}
