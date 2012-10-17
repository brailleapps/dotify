package org.daisy.dotify.input;

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

class DefaultInputManagerFactoryMaker extends InputManagerFactoryMaker {
	private final List<InputManagerFactory> filters;
	private final Map<String, InputManagerFactory> map;
	private final Logger logger;
	
	public DefaultInputManagerFactoryMaker() {
		logger = Logger.getLogger(InputManagerFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<InputManagerFactory>();
		Iterator<InputManagerFactory> i = ServiceRegistry.lookupProviders(InputManagerFactory.class);
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<String, InputManagerFactory>();
	}
	
	private static String toKey(FilterLocale context, String fileFormat) {
		return context.toString() + "(" + fileFormat + ")";
	}
	
	public InputManagerFactory getFactory(FilterLocale locale, String fileFormat) {
		InputManagerFactory template = map.get(toKey(locale, fileFormat));
		if (template==null) {
			for (InputManagerFactory h : filters) {
				if (h.supportsSpecification(locale, fileFormat)) {
					logger.fine("Found a factory for " + locale + " (" + h.getClass() + ")");
					map.put(toKey(locale, fileFormat), h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new IllegalArgumentException("Cannot locate an InputManager for " + locale);
		}
		return template;
	}
	
	public Set<String> listSupportedLocales() {
		HashSet<String> ret = new HashSet<String>();
		for (InputManagerFactory h : filters) {
			ret.addAll(h.listSupportedLocales());
		}
		return ret;
	}

}