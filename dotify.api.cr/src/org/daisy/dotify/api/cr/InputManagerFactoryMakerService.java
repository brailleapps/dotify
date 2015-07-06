package org.daisy.dotify.api.cr;

import java.util.Set;

/**
 * <p>
 * Provides an interface for a InputManagerFactoryMaker service. The purpose of
 * this interface is to expose an implementation of a InputManagerFactoryMaker as
 * an OSGi service.
 * </p>
 * 
 * <p>
 * To comply with this interface, an implementation must be thread safe and
 * address both the possibility that only a single instance is created and used
 * throughout and that new instances are created as desired.
 * </p>
 * 
 * @author Joel Håkansson
 * 
 */
public interface InputManagerFactoryMakerService {

	/**
	 * Gets a InputManagerFactory that supports the specification
	 * 
	 * @param specification the specification
	 * @return returns a input manager factory for the specified locale and format
	 */
	public InputManagerFactory getFactory(TaskGroupSpecification specification);

	/**
	 * Creates a new input manager with the specified options.
	 * @param specification the specification
	 * @return returns a new input manager
	 * @throws IllegalArgumentException if the specified configuration isn't supported
	 */
	public InputManager newInputManager(TaskGroupSpecification specification);

	/**
	 * Gets a list of supported specifications.
	 * @return returns a list of supported specifications
	 */
	public Set<TaskGroupSpecification> listSupportedSpecifications();

}
