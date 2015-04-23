package org.daisy.dotify.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Logger;

import org.daisy.dotify.common.text.FilterLocale;

/**
 * Provides a factory maker for input manager factories, that is to say a collection of 
 * @author joha
 *
 */
public abstract class InputManagerFactoryMaker {

	protected InputManagerFactoryMaker() { }

	public final static InputManagerFactoryMaker newInstance() {
		Iterator<InputManagerFactoryMaker> i = ServiceLoader.load(InputManagerFactoryMaker.class).iterator();
		while (i.hasNext()) {
			return i.next();
		}
		return new DefaultInputManagerFactoryMaker();
	}

	public abstract InputManagerFactory getFactory(FilterLocale locale, String fileFormat);
	
	public abstract Set<String> listSupportedLocales();

	public abstract Set<String> listSupportedFileFormats();

	public InputManager newInputManager(FilterLocale locale, String fileFormat) {
		Logger.getLogger(this.getClass().getCanonicalName()).fine("Attempt to locate an input manager for " + locale + "/" + fileFormat);
		return getFactory(locale, fileFormat).newInputManager(locale, fileFormat);
	}

	private static class DefaultInputManagerFactoryMaker extends InputManagerFactoryMaker {
		private final List<InputManagerFactory> filters;
		private final Map<String, InputManagerFactory> map;
		private final Logger logger;
		
		public DefaultInputManagerFactoryMaker() {
			logger = Logger.getLogger(InputManagerFactoryMaker.class.getCanonicalName());
			filters = new ArrayList<InputManagerFactory>();
			Iterator<InputManagerFactory> i = ServiceLoader.load(InputManagerFactory.class).iterator();
			while (i.hasNext()) {
				filters.add(i.next());
			}
			this.map = new HashMap<String, InputManagerFactory>();
		}
		
		private static String toKey(FilterLocale context, String fileFormat) {
			return context.toString() + "(" + fileFormat + ")";
		}
		
		public InputManagerFactory getFactory(FilterLocale locale, String fileFormat) {
			InputManagerFactory template = map.get(toKey(locale, fileFormat));
			if (template==null) {
				for (InputManagerFactory h : filters) {
					if (h.supportsSpecification(locale, fileFormat)) {
						logger.fine("Found a factory for " + locale + " (" + h.getClass() + ")");
						map.put(toKey(locale, fileFormat), h);
						template = h;
						break;
					}
				}
			}
			if (template==null) {
				throw new IllegalArgumentException("Cannot locate an InputManager for " + locale + "/" + fileFormat);
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

		public Set<String> listSupportedFileFormats() {
			HashSet<String> ret = new HashSet<String>();
			for (InputManagerFactory h : filters) {
				ret.addAll(h.listSupportedFileFormats());
			}
			return ret;
		}

	}
}
