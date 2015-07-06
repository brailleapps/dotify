package org.daisy.dotify.consumer.cr;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import org.daisy.dotify.api.cr.InputManager;
import org.daisy.dotify.api.cr.InputManagerFactory;
import org.daisy.dotify.api.cr.InputManagerFactoryMakerService;
import org.daisy.dotify.api.cr.TaskGroupSpecification;

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
		filters = new CopyOnWriteArrayList<InputManagerFactory>();
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

	private static String toKey(TaskGroupSpecification spec) {
		return new StringBuilder().
				append(spec.getLocale()).
				append(" (").
				append(spec.getInputFormat()).
				append(" -> ").
				append(spec.getOutputFormat()).
				append(")").toString();
	}
	
	@Override
	public InputManagerFactory getFactory(TaskGroupSpecification spec) {
		String specKey = toKey(spec);
		InputManagerFactory template = map.get(specKey);
		if (template==null) {
			// this is to avoid adding items to the cache that were removed
			// while iterating
			synchronized (map) {
				for (InputManagerFactory h : filters) {
					if (h.supportsSpecification(spec)) {
						logger.fine("Found a factory for " + specKey + " (" + h.getClass() + ")");
						map.put(specKey, h);
						template = h;
						break;
					}
				}
			}
		}
		if (template==null) {
			throw new IllegalArgumentException("Cannot locate an InputManager for " + toKey(spec));
		}
		return template;
	}
	
	@Override
	public InputManager newInputManager(TaskGroupSpecification spec) {
		logger.fine("Attempt to locate an input manager for " + toKey(spec));
		return getFactory(spec).newInputManager(spec);
	}
	
	@Override
	public Set<TaskGroupSpecification> listSupportedSpecifications() {
		HashSet<TaskGroupSpecification> ret = new HashSet<TaskGroupSpecification>();
		for (InputManagerFactory h : filters) {
			ret.addAll(h.listSupportedSpecifications());
		}
		return ret;
	}

}