package org.daisy.dotify.translator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;

public class BrailleTranslatorFactoryMaker {
	private final List<BrailleTranslatorFactory> factories;
	private final Map<String, BrailleTranslatorFactory> map;
	private final Logger logger;

	private BrailleTranslatorFactoryMaker() {
		logger = Logger.getLogger(BrailleTranslatorFactoryMaker.class.getCanonicalName());
		factories = new ArrayList<BrailleTranslatorFactory>();
		Iterator<BrailleTranslatorFactory> i = ServiceRegistry.lookupProviders(BrailleTranslatorFactory.class);
		while (i.hasNext()) {
			factories.add(i.next());
		}
		this.map = new HashMap<String, BrailleTranslatorFactory>();
	}

	public static BrailleTranslatorFactoryMaker newInstance() {
		Iterator<BrailleTranslatorFactoryMaker> i = ServiceRegistry.lookupProviders(BrailleTranslatorFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new BrailleTranslatorFactoryMaker();
	}
	
	private static String toKey(FilterLocale locale, String grade) {
		return locale.toString() + "(" + grade + ")";
	}
	
	public boolean supportsSpecification(FilterLocale locale, String grade) {
		return map.get(toKey(locale, grade))!=null;
	}
	
	public BrailleTranslatorFactory getFactory(FilterLocale locale, String grade) throws UnsupportedSpecificationException {
		BrailleTranslatorFactory template = map.get(toKey(locale, grade));
		if (template==null) {
			for (BrailleTranslatorFactory h : factories) {
				if (h.supportsSpecification(locale, grade)) {
					logger.fine("Found a factory for " + locale + " (" + h.getClass() + ")");
					map.put(toKey(locale, grade), h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new UnsupportedSpecificationException("Cannot locate a factory for " + toKey(locale, grade));
		}
		return template;
	}
	
	/**
	 *  Instantiates a new braille translator with the given specification.
	 *  @param locale the translator locale
	 *  @param grade the translator grade, or null
	 *  @throws UnsupportedSpecificationException if the specification is not supported
	 */
	public BrailleTranslator newBrailleTranslator(FilterLocale locale, String grade) throws UnsupportedSpecificationException {
		return getFactory(locale, grade).newTranslator(locale, grade);
	}
}
