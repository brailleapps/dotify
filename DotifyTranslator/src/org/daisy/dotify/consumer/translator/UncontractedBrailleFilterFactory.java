package org.daisy.dotify.consumer.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.api.translator.StringFilter;
import org.daisy.dotify.api.translator.StringFilterFactory;
import org.daisy.dotify.api.translator.UncontractedBrailleFilter;


/**
 * Provides a factory for braille StringFilters. It can return different StringFilters
 * depending on the requested locale. Allow access to all locale rules and optionally output braille using that locale.
 * @author Joel Håkansson, TPB
 */
public class UncontractedBrailleFilterFactory implements StringFilterFactory {
	private final ArrayList<UncontractedBrailleFilter> filters;
	private final HashMap<String, UncontractedBrailleFilter> cache;
	private final Logger logger;

	protected UncontractedBrailleFilterFactory() {
		logger = Logger.getLogger(UncontractedBrailleFilterFactory.class.getCanonicalName());
		filters = new ArrayList<UncontractedBrailleFilter>();
		Iterator<UncontractedBrailleFilter> i = ServiceRegistry.lookupProviders(UncontractedBrailleFilter.class);
		UncontractedBrailleFilter f;
		while (i.hasNext()) {
			f = i.next();
			filters.add(f);
		}
		this.cache = new HashMap<String, UncontractedBrailleFilter>();
	}

	/**
	 * Gets a new instance of BrailleFilterFactory
	 * @return returns a new instance of BrailleFilterFactory
	 */
	public static UncontractedBrailleFilterFactory newInstance() {
		Iterator<UncontractedBrailleFilterFactory> i = ServiceRegistry.lookupProviders(UncontractedBrailleFilterFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new UncontractedBrailleFilterFactory();
	}

	/**
	 * Attempts to retrieve a StringFilter for the given locale.
	 * @param target target locale
	 * @return returns a StringFilter for the given locale
	 * @throws IllegalArgumentException if no match is found
	 */
	public StringFilter newStringFilter(String target) {
		if (cache.containsKey(target)) {
			return cache.get(target);
		}
		for (UncontractedBrailleFilter ret : filters) {
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