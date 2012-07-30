package org.daisy.dotify.hyphenator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;

public class HyphenatorFactory {
	private final List<HyphenatorInterface> filters;
	private final Map<FilterLocale, HyphenatorInterface> map;
	private final Logger logger;
	
	//private int beginLimit, endLimit;
	
	protected HyphenatorFactory() {
		logger = Logger.getLogger(HyphenatorFactory.class.getCanonicalName());
		filters = new ArrayList<HyphenatorInterface>();
		Iterator<HyphenatorInterface> i = ServiceRegistry.lookupProviders(HyphenatorInterface.class);
		HyphenatorInterface f;
		while (i.hasNext()) {
			f = i.next();
			filters.add(f);
		}
		this.map = new HashMap<FilterLocale, HyphenatorInterface>();
		//beginLimit = 2;
		//endLimit = 2;
	}
	
	public static HyphenatorFactory newInstance() {
		Iterator<HyphenatorFactory> i = ServiceRegistry.lookupProviders(HyphenatorFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new HyphenatorFactory();
	}
/*
	
	public int getBeginLimit() {
		return beginLimit;
	}

	public void setBeginLimit(int beginLimit) {
		this.beginLimit = beginLimit;
	}

	public int getEndLimit() {
		return endLimit;
	}

	public void setEndLimit(int endLimit) {
		this.endLimit = endLimit;
	}
*/
	public HyphenatorInterface newHyphenator(FilterLocale target) throws UnsupportedLocaleException {
		HyphenatorInterface template = map.get(target);
		if (template==null) {
			for (HyphenatorInterface h : filters) {
				if (h.supportsLocale(target)) {
					logger.fine("Found a hyphenator for " + target + " (" + h.getClass() + ")");
					map.put(target, h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new IllegalArgumentException("Cannot find hyphenator for " + target);
		} else {
			HyphenatorInterface ret = template.newInstance(target);
			//ret.setBeginLimit(beginLimit);
			//ret.setEndLimit(endLimit);
			return ret;
		}
	}

}
