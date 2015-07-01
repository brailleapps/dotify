package org.daisy.dotify.api.cr;


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
	public boolean supportsSpecification(String locale, String outputFormat);
	
	/**
	 * Creates a new task system with the given properties.
	 * @param locale the desired locale
	 * @param outputFormat the desired output format
	 * @return returns a new task system
	 * @throws TaskSystemFactoryException if a task system with these properties cannot be created
	 */
	public TaskSystem newTaskSystem(String locale, String outputFormat) throws TaskSystemFactoryException;
	
}
