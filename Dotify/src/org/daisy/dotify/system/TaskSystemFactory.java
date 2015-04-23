package org.daisy.dotify.system;

import org.daisy.dotify.common.text.FilterLocale;

/**
 * Provides an interface for task system factories. A
 * task system factory implementation can provide
 * task system instances for any number of specifications.
 * 
 * @author Joel Håkansson
 */
public interface TaskSystemFactory {
	
	/**
	 * Returns true if this factory can create instances with the desired properties.
	 * @param locale the desired locale
	 * @param outputFormat the desired output format
	 * @return returns true if this factory can create instances with the desired properties, false otherwise
	 */
	public boolean supportsSpecification(FilterLocale locale, String outputFormat);
	
	/**
	 * Creates a new task system with the given properties.
	 * @param locale the desired locale
	 * @param outputFormat the desired output format
	 * @return returns a new task system
	 * @throws TaskSystemFactoryException if a task system with these properties cannot be created
	 */
	public TaskSystem newTaskSystem(FilterLocale locale, String outputFormat) throws TaskSystemFactoryException;
	
}
