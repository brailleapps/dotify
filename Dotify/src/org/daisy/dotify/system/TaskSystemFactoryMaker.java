package org.daisy.dotify.system;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.logging.Logger;

import org.daisy.dotify.text.FilterLocale;


/**
 * Entry point for retrieving a TaskSystem implementation. This class will
 * locate all TaskSystemFactory implementations available to the java
 * services API.
 * 
 * @author Joel Håkansson
 *
 */
public class TaskSystemFactoryMaker {
	private final List<TaskSystemFactory> filters;
	private final Map<String, TaskSystemFactory> map;
	private final Logger logger;

	private TaskSystemFactoryMaker() {
		logger = Logger.getLogger(TaskSystemFactoryMaker.class.getCanonicalName());
		filters = new ArrayList<TaskSystemFactory>();
		Iterator<TaskSystemFactory> i = ServiceLoader.load(TaskSystemFactory.class).iterator();
		while (i.hasNext()) {
			filters.add(i.next());
		}
		this.map = new HashMap<String, TaskSystemFactory>();
	}

	public static TaskSystemFactoryMaker newInstance() {
		Iterator<TaskSystemFactoryMaker> i = ServiceLoader.load(TaskSystemFactoryMaker.class).iterator();
		while (i.hasNext()) {
			return i.next();
		}
		return new TaskSystemFactoryMaker();
	}
	
	private static String toKey(FilterLocale context, String outputFormat) {
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
	 *  Gets a task system for the specified output format and context
	 */
	public TaskSystem newTaskSystem(String outputFormat, FilterLocale context) throws TaskSystemFactoryException {
		return getFactory(context, outputFormat).newTaskSystem(context, outputFormat);
	}
}