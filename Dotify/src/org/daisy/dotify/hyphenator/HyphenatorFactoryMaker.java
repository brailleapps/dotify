package org.daisy.dotify.hyphenator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;

public class HyphenatorFactoryMaker {
	private final List<HyphenatorFactory> filters;
	private final Map<FilterLocale, HyphenatorFactory> map;
	private final Logger logger;
	
	private Integer beginLimit, endLimit;
	
	protected HyphenatorFactoryMaker() {
		logger = Logger.getLogger(HyphenatorFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<HyphenatorFactory>();
		Iterator<HyphenatorFactory> i = ServiceRegistry.lookupProviders(HyphenatorFactory.class);
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<FilterLocale, HyphenatorFactory>();
		beginLimit = null;
		endLimit = null;
	}
	
	public static HyphenatorFactoryMaker newInstance() {
		Iterator<HyphenatorFactoryMaker> i = ServiceRegistry.lookupProviders(HyphenatorFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new HyphenatorFactoryMaker();
	}

	public Integer getBeginLimit() {
		return beginLimit;
	}

	public void setBeginLimit(Integer beginLimit) {
		this.beginLimit = beginLimit;
	}

	public Integer getEndLimit() {
		return endLimit;
	}

	public void setEndLimit(Integer endLimit) {
		this.endLimit = endLimit;
	}
	
	/**
	 * Gets a HyphenatorFactory that supports the specified locale
	 * @param target
	 * @return
	 */
	public HyphenatorFactory getFactory(FilterLocale target) {
		HyphenatorFactory template = map.get(target);
		if (template==null) {
			for (HyphenatorFactory h : filters) {
				if (h.supportsLocale(target)) {
					logger.fine("Found a hyphenator factory for " + target + " (" + h.getClass() + ")");
					map.put(target, h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new IllegalArgumentException("Cannot find hyphenator factory for " + target);
		}
		return template;
	}

	public HyphenatorInterface newHyphenator(FilterLocale target) throws UnsupportedLocaleException {
		HyphenatorInterface ret = getFactory(target).newHyphenator(target);
		if (beginLimit!=null) {
			ret.setBeginLimit(beginLimit);
		}
		if (endLimit!=null) {
			ret.setEndLimit(endLimit);
		}
		return ret;
	}

}
