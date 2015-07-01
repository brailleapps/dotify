package org.daisy.dotify.consumer.cr;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.logging.Logger;

import org.daisy.dotify.api.cr.InputManager;
import org.daisy.dotify.api.cr.InputManagerFactory;
import org.daisy.dotify.api.cr.InputManagerFactoryMakerService;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * Provides a factory maker for input manager factories, that is to say a collection of
 *  
 * @author Joel Håkansson
 */
@Component
public class InputManagerFactoryMaker implements InputManagerFactoryMakerService {
	private final List<InputManagerFactory> filters;
	private final Map<String, InputManagerFactory> map;
	private final Logger logger;

	public InputManagerFactoryMaker() {
		logger = Logger.getLogger(InputManagerFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<InputManagerFactory>();
		this.map = Collections.synchronizedMap(new HashMap<String, InputManagerFactory>());		
	}

	/**
	 * <p>
	 * Creates a new InputManagerFactoryMaker and populates it using the SPI (java
	 * service provider interface).
	 * </p>
	 * 
	 * <p>
	 * In an OSGi context, an instance should be retrieved using the service
	 * registry. It will be registered under the InputManagerFactoryMakerService
	 * interface.
	 * </p>
	 * 
	 * @return returns a new InputManagerFactoryMaker
	 */
	public final static InputManagerFactoryMaker newInstance() {
		InputManagerFactoryMaker ret = new InputManagerFactoryMaker();
		{
			Iterator<InputManagerFactory> i = ServiceLoader.load(InputManagerFactory.class).iterator();
			while (i.hasNext()) {
				ret.addFactory(i.next());
			}
		}
		return ret;
	}

	@Reference(type = '*')
	public void addFactory(InputManagerFactory factory) {
		logger.finer("Adding factory: " + factory);
		filters.add(factory);
	}

	// Unbind reference added automatically from addFactory annotation
	public void removeFactory(InputManagerFactory factory) {
		logger.finer("Removing factory: " + factory);
		// this is to avoid adding items to the cache that were removed while
		// iterating
		synchronized (map) {
			filters.remove(factory);
			map.clear();
		}
	}
		
	private static String toKey(String context, String fileFormat) {
		return context + "(" + fileFormat + ")";
	}
	
	public InputManagerFactory getFactory(String locale, String fileFormat) {
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
	
	public InputManager newInputManager(String locale, String fileFormat) {
		logger.fine("Attempt to locate an input manager for " + locale + "/" + fileFormat);
		return getFactory(locale, fileFormat).newInputManager(locale, fileFormat);
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