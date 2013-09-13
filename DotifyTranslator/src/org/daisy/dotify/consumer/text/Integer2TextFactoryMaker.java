package org.daisy.dotify.consumer.text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.api.text.Integer2Text;
import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.daisy.dotify.api.text.Integer2TextFactory;

/**
 * Provides a integer2text factory maker. This is the entry point for
 * creating integer2text instances.
 * 
 * @author Joel Håkansson
 */
public class Integer2TextFactoryMaker {
	private final List<Integer2TextFactory> filters;
	private final Map<String, Integer2TextFactory> map;
	private final Logger logger;

	protected Integer2TextFactoryMaker() {
		logger = Logger.getLogger(Integer2TextFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<Integer2TextFactory>();
		Iterator<Integer2TextFactory> i = ServiceRegistry.lookupProviders(Integer2TextFactory.class);
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<String, Integer2TextFactory>();
	}
	
	/**
	 * Creates a new integer2text factory maker.
	 * 
	 * @return returns a new integer2text factory maker
	 */
	public static Integer2TextFactoryMaker newInstance() {
		Iterator<Integer2TextFactoryMaker> i = ServiceRegistry.lookupProviders(Integer2TextFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new Integer2TextFactoryMaker();
	}
	
	/**
	 * Gets a Integer2TextFactory that supports the specified locale
	 * 
	 * @param target
	 *            the target locale
	 * @return returns a integer2text factory for the specified locale
	 * @throws Integer2TextFactoryMakerException
	 *             if the locale is not supported
	 */
	public Integer2TextFactory getFactory(String target) throws Integer2TextConfigurationException {
		Integer2TextFactory template = map.get(target);
		if (template==null) {
			for (Integer2TextFactory h : filters) {
				if (h.supportsLocale(target)) {
					logger.fine("Found an integer2text factory for " + target + " (" + h.getClass() + ")");
					map.put(target, h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new Integer2TextFactoryMakerConfigurationException("Cannot find integer2text factory for " + target);
		}
		return template;
	}

	/**
	 * Creates a new integer2text. This is a convenience method for
	 * getFactory(target).newInteger2Text(target).
	 * Using this method excludes the possibility of setting features of the
	 * integer2text factory.
	 * 
	 * @param target
	 *            the target locale
	 * @return returns a new integer2text
	 * @throws UnsupportedLocaleException
	 *             if the locale is not supported
	 */
	public Integer2Text newInteger2Text(String target) throws Integer2TextConfigurationException {
		return getFactory(target).newInteger2Text(target);
	}
	
	private class Integer2TextFactoryMakerConfigurationException extends Integer2TextConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 1296708210640963472L;

		Integer2TextFactoryMakerConfigurationException(String message) {
			super(message);
		}
		
	}

}
