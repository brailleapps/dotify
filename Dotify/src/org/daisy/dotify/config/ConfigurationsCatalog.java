package org.daisy.dotify.config;

import java.net.URL;
import java.util.Iterator;
import java.util.Set;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.system.ResourceLocatorException;

public abstract class ConfigurationsCatalog {

	public final static ConfigurationsCatalog newInstance() {
		Iterator<ConfigurationsCatalog> i = ServiceRegistry.lookupProviders(ConfigurationsCatalog.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new DefaultConfigurationsCatalog();
	}
	
	public abstract Set<String> getKeys();
	public abstract URL getConfigurationURL(String identifier) throws ResourceLocatorException;

}
