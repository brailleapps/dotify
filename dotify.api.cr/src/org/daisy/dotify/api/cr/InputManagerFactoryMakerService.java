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
	 * Gets a InputManagerFactory that supports the specified locale and format.
	 * 
	 * @param locale the target locale
	 * @param fileFormat the input file format
	 * @return returns a input manager factory for the specified locale and format
	 */
	public InputManagerFactory getFactory(String locale, String fileFormat);

	/**
	 * Creates a new input manager with the specified options.
	 * @param locale the target locale
	 * @param fileFormat the input file format
	 * @return returns a new input manager
	 * @throws IllegalArgumentException if the specified configuration isn't supported
	 */
	public InputManager newInputManager(String locale, String fileFormat);

	/**
	 * Gets a list of supported locales.
	 * @return returns a list of supported locales
	 */
	public Set<String> listSupportedLocales();

	/**
	 * Gets a list of supported input formats.
	 * @return returns a list of supported input formats
	 */
	public Set<String> listSupportedFileFormats();

}
