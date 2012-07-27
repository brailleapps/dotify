package org.daisy.dotify.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterFactory;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.StringFilter;


/**
 * Provides a factory for braille StringFilters. It can return different StringFilters
 * depending on the requested locale. Allow access to all locale rules and optionally output braille using that locale.
 * @author Joel Håkansson, TPB
 */
public class BrailleFilterFactory implements FilterFactory {
	private final ArrayList<BrailleFilter> filters;
	private final HashMap<FilterLocale, BrailleFilter> cache;
	private final Logger logger;

	protected BrailleFilterFactory() {
		logger = Logger.getLogger(BrailleFilterFactory.class.getCanonicalName());
		filters = new ArrayList<BrailleFilter>();
		Iterator<BrailleFilter> i = ServiceRegistry.lookupProviders(BrailleFilter.class);
		BrailleFilter f;
		while (i.hasNext()) {
			f = i.next();
			filters.add(f);
		}
		this.cache = new HashMap<FilterLocale, BrailleFilter>();
	}

	/**
	 * Gets a new instance of BrailleFilterFactory
	 * @return returns a new instance of BrailleFilterFactory
	 */
	public static BrailleFilterFactory newInstance() {
		Iterator<BrailleFilterFactory> i = ServiceRegistry.lookupProviders(BrailleFilterFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new BrailleFilterFactory();
	}

	/**
	 * Attempts to retrieve a StringFilter for the given locale.
	 * @param target target locale
	 * @return returns a StringFilter for the given locale
	 * @throws IllegalArgumentException if no match is found
	 */
	public StringFilter newStringFilter(FilterLocale target) {
		if (cache.containsKey(target)) {
			return cache.get(target);
		}
		for (BrailleFilter ret : filters) {
			if (ret.supportsLocale(target)) {
				logger.fine("Found a StringFilter for " + target + " (" + ret.getClass() + ")");
				ret.setLocale(target);
				cache.put(target, ret);
				return ret;
			}
		}
		throw new IllegalArgumentException("Cannot find filter for " + target);
	}

}