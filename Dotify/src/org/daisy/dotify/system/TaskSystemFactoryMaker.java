package org.daisy.dotify.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;


/**
 * Entry point for retrieving a TaskSystem implementation. Modify this file to 
 * add new TaskSystems.
 * @author Joel Håkansson, TPB
 *
 */
public class TaskSystemFactoryMaker {
	private final List<TaskSystemFactory> filters;
	private final Map<String, TaskSystemFactory> map;
	private final Logger logger;

	private TaskSystemFactoryMaker() {
		logger = Logger.getLogger(TaskSystemFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<TaskSystemFactory>();
		Iterator<TaskSystemFactory> i = ServiceRegistry.lookupProviders(TaskSystemFactory.class);
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<String, TaskSystemFactory>();
	}

	public static TaskSystemFactoryMaker newInstance() {
		Iterator<TaskSystemFactoryMaker> i = ServiceRegistry.lookupProviders(TaskSystemFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new TaskSystemFactoryMaker();
	}
	
	private String toKey(FilterLocale context, String outputFormat) {
		return context.toString() + "(" + outputFormat + ")";
	}
	
	public TaskSystemFactory getFactory(FilterLocale locale, String outputFormat) throws TaskSystemFactoryException {
		TaskSystemFactory template = map.get(toKey(locale, outputFormat));
		if (template==null) {
			for (TaskSystemFactory h : filters) {
				if (h.supportsSpecification(locale, outputFormat)) {
					logger.fine("Found a factory for " + locale + " (" + h.getClass() + ")");
					map.put(toKey(locale, outputFormat), h);
					template = h;
					break;
				}
			}
		}
		if (template==null) {
			throw new TaskSystemFactoryException("Cannot locate a TaskSystemFactory for " + locale + "/" +outputFormat);
		}
		return template;
	}
	
	/**
	 *  System setups are defined here.
	 *  
	 *  Each system setup consists of a series of tasks that, put together, performs a format conversion. 
	 *  A system is labeled by an identifier when inserted into the HashMap.
	 *  The recommended practice is to use a language region (or sub region) as identifier.
	 *
	 *  New system setups can be added to the conversion system by following the example below.
	 */
	public TaskSystem newTaskSystem(String outputFormat, FilterLocale context) throws TaskSystemFactoryException {
		return getFactory(context, outputFormat).newTaskSystem(context, outputFormat);
	}
}