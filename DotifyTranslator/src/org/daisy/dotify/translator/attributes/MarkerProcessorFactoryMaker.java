package org.daisy.dotify.translator.attributes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.TranslatorConfigurationException;

/**
 * Provides a marker processor factory maker. This class will look for
 * implementations of the MarkerProcessorFactory interface using the
 * services API. It will return the first implementation that matches the
 * requested specification.
 * 
 * <p>
 * This class can be overridden by extending it and adding a reference to the
 * new implementation to the services API. This class will then choose the new
 * implementation when a new instance is requested.
 * </p>
 * 
 * @author Joel Håkansson
 * 
 */
public class MarkerProcessorFactoryMaker implements MarkerProcessorFactory {
	private final List<MarkerProcessorFactory> factories;
	private final Map<String, MarkerProcessorFactory> map;
	private final Logger logger;

	private MarkerProcessorFactoryMaker() {
		logger = Logger.getLogger(MarkerProcessorFactoryMaker.class.getCanonicalName());
		factories = new ArrayList<MarkerProcessorFactory>();
		Iterator<MarkerProcessorFactory> i = ServiceRegistry.lookupProviders(MarkerProcessorFactory.class);
		while (i.hasNext()) {
			factories.add(i.next());
		}
		this.map = new HashMap<String, MarkerProcessorFactory>();
	}

	/**
	 * Creates a new instance of marker processor factory maker.
	 * @return returns a new marker processor factory maker.
	 */
	public static MarkerProcessorFactoryMaker newInstance() {
		Iterator<MarkerProcessorFactoryMaker> i = ServiceRegistry.lookupProviders(MarkerProcessorFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new MarkerProcessorFactoryMaker();
	}
	
	private static String toKey(FilterLocale locale, String grade) {
		return locale.toString() + "(" + grade + ")";
	}
	
	public boolean supportsSpecification(FilterLocale locale, String grade) {
		return map.get(toKey(locale, grade))!=null;
	}
	
	/**
	 * Gets a factory for the given specification.
	 * 
	 * @param locale the locale for the factory
	 * @param grade the grade for the factory
	 * @return returns a marker processor factory
	 * @throws TranslatorConfigurationException if the specification is not supported
	 */
	public MarkerProcessorFactory getFactory(FilterLocale locale, String grade) throws MarkerProcessorFactoryMakerException {
		MarkerProcessorFactory template = map.get(toKey(locale, grade));
		if (template==null) {
			for (MarkerProcessorFactory h : factories) {
				if (h.supportsSpecification(locale, grade)) {
					logger.fine("Found a factory for " + locale + " (" + h.getClass() + ")");
					map.put(toKey(locale, grade), h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new MarkerProcessorFactoryMakerException("Cannot locate a factory for " + toKey(locale, grade));
		}
		return template;
	}
	
	public MarkerProcessor newMarkerProcessor(FilterLocale locale, String grade) throws MarkerProcessorConfigurationException {
		return getFactory(locale, grade).newMarkerProcessor(locale, grade);
	}
}
