package org.daisy.dotify.api.cr;

import java.util.Set;

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
