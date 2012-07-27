package org.daisy.dotify.hyphenator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;

public class HyphenatorFactory {
	private final ArrayList<HyphenatorInterface> filters;
	private final HashMap<FilterLocale, HyphenatorInterface> cache;
	private final Logger logger;
	
	protected HyphenatorFactory() {
		logger = Logger.getLogger(HyphenatorFactory.class.getCanonicalName());
		filters = new ArrayList<HyphenatorInterface>();
		Iterator<HyphenatorInterface> i = ServiceRegistry.lookupProviders(HyphenatorInterface.class);
		HyphenatorInterface f;
		while (i.hasNext()) {
			f = i.next();
			filters.add(f);
		}
		this.cache = new HashMap<FilterLocale, HyphenatorInterface>();

	}
	
	public static HyphenatorFactory newInstance() {
		Iterator<HyphenatorFactory> i = ServiceRegistry.lookupProviders(HyphenatorFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new HyphenatorFactory();
	}

	public HyphenatorInterface getHyphenator(FilterLocale target) {
		if (cache.containsKey(target)) {
			return cache.get(target);
		}
		for (HyphenatorInterface ret : filters) {
			if (ret.supportsLocale(target)) {
				logger.fine("Found a hyphenator for " + target + " (" + ret.getClass() + ")");
				cache.put(target, ret);
				return ret;
			}
		}
		throw new IllegalArgumentException("Cannot find hyphenator for " + target);
	}

}
