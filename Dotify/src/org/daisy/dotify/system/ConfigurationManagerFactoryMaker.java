package org.daisy.dotify.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;

public class ConfigurationManagerFactoryMaker {
	private final List<ConfigurationManagerFactory> filters;
	private final Map<FilterLocale, ConfigurationManagerFactory> map;
	private final Logger logger;
	
	protected ConfigurationManagerFactoryMaker() {
		logger = Logger.getLogger(ConfigurationManagerFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<ConfigurationManagerFactory>();
		Iterator<ConfigurationManagerFactory> i = ServiceRegistry.lookupProviders(ConfigurationManagerFactory.class);
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<FilterLocale, ConfigurationManagerFactory>();
	}

	public static ConfigurationManagerFactoryMaker newInstance() {
		Iterator<ConfigurationManagerFactoryMaker> i = ServiceRegistry.lookupProviders(ConfigurationManagerFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new ConfigurationManagerFactoryMaker();
	}

	public ConfigurationManagerFactory getFactory(FilterLocale locale) {
		ConfigurationManagerFactory template = map.get(locale);
		if (template==null) {
			for (ConfigurationManagerFactory h : filters) {
				if (h.supportsLocale(locale)) {
					logger.fine("Found a factory for " + locale + " (" + h.getClass() + ")");
					map.put(locale, h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new IllegalArgumentException("Cannot locate an ConfigurationManager for " + locale);
		}
		return template;
	}
	
	public Set<String> listSupportedLocales() {
		HashSet<String> ret = new HashSet<String>();
		for (ConfigurationManagerFactory h : filters) {
			ret.addAll(h.listSupportedLocales());
		}
		return ret;
	}
	
	public ConfigurationManager newConfigurationManager(FilterLocale locale) {
		return getFactory(locale).newConfigurationManager(locale);
	}

}
