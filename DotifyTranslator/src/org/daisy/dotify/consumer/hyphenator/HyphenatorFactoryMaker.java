package org.daisy.dotify.consumer.hyphenator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactory;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;

/**
 * Provides a hyphenator factory maker. This is the entry point for
 * creating hyphenator instances.
 * 
 * @author Joel Håkansson
 */
public class HyphenatorFactoryMaker {
	private final List<HyphenatorFactory> filters;
	private final Map<String, HyphenatorFactory> map;
	private final Logger logger;
	
	private Integer beginLimit, endLimit;
	
	protected HyphenatorFactoryMaker() {
		logger = Logger.getLogger(HyphenatorFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<HyphenatorFactory>();
		Iterator<HyphenatorFactory> i = ServiceRegistry.lookupProviders(HyphenatorFactory.class);
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<String, HyphenatorFactory>();
		beginLimit = null;
		endLimit = null;
	}
	
	/**
	 * Creates a new hyphenator factory maker.
	 * @return returns a new hyphenator factory maker
	 */
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
	 * 
	 * @param target
	 *            the target locale
	 * @return returns a hyphenator factory for the specified locale
	 * @throws HyphenatorConfigurationException
	 *             if the locale is not supported
	 */
	public HyphenatorFactory getFactory(String target) throws HyphenatorConfigurationException {
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
			throw new HyphenatorFactoryMakerConfigurationException("Cannot find hyphenator factory for " + target);
		}
		return template;
	}

	/**
	 * Creates a new hyphenator. This is a convenience method for
	 * getFactory(target).newHyphenator(target).
	 * Using this method excludes the possibility of setting features of the
	 * hyphenator factory.
	 * 
	 * @param target
	 *            the target locale
	 * @return returns a new hyphenator
	 * @throws HyphenatorConfigurationException
	 *             if the locale is not supported
	 */
	public HyphenatorInterface newHyphenator(String target) throws HyphenatorConfigurationException {
		HyphenatorInterface ret = getFactory(target).newHyphenator(target);
		if (beginLimit!=null) {
			ret.setBeginLimit(beginLimit);
		}
		if (endLimit!=null) {
			ret.setEndLimit(endLimit);
		}
		return ret;
	}
	
	private class HyphenatorFactoryMakerConfigurationException extends HyphenatorConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 8869650439082769112L;

		HyphenatorFactoryMakerConfigurationException(String message) {
			super(message);
			// TODO Auto-generated constructor stub
		}
		
	}

}
