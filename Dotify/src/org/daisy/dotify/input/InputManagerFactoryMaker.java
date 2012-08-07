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

public class InputManagerFactoryMaker {
	private final List<InputManagerFactory> filters;
	private final Map<FilterLocale, InputManagerFactory> map;
	private final Logger logger;
	
	protected InputManagerFactoryMaker() {
		logger = Logger.getLogger(InputManagerFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<InputManagerFactory>();
		Iterator<InputManagerFactory> i = ServiceRegistry.lookupProviders(InputManagerFactory.class);
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<FilterLocale, InputManagerFactory>();
	}

	public static InputManagerFactoryMaker newInstance() {
		Iterator<InputManagerFactoryMaker> i = ServiceRegistry.lookupProviders(InputManagerFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new InputManagerFactoryMaker();
	}

	public InputManagerFactory getFactory(FilterLocale locale) {
		InputManagerFactory template = map.get(locale);
		if (template==null) {
			for (InputManagerFactory h : filters) {
				if (h.supportsLocale(locale)) {
					logger.fine("Found a factory for " + locale + " (" + h.getClass() + ")");
					map.put(locale, h);
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
	
	public InputManager newInputManager(FilterLocale locale) {
		return getFactory(locale).newInputManager(locale);
	}

}
