package org.daisy.dotify.system;

import org.daisy.dotify.text.FilterLocale;

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
