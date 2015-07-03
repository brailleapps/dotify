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

	/**
	 * <p>Informs the implementation that it was discovered and instantiated using
	 * information collected from a file within the <tt>META-INF/services</tt> directory.
	 * In other words, it was created using SPI (service provider interfaces).</p>
	 * 
	 * <p>This information, in turn, enables the implementation to use the same mechanism
	 * to set dependencies as needed.</p>
	 * 
	 * <p>If this information is <strong>not</strong> given, an implementation
	 * should avoid using SPIs and instead use
	 * <a href="http://wiki.osgi.org/wiki/Declarative_Services">declarative services</a>
	 * for dependency injection as specified by OSGi. Note that this also applies to
	 * several newInstance() methods in the Java API.</p>
	 * 
	 * <p>The class that created an instance with SPI must call this method before
	 * putting it to use.</p>
	 */
	public void setCreatedWithSPI();

}
