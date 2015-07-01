package org.daisy.dotify.api.cr;

import java.util.Set;

/**
 * <p>Provides an interface for input manager factories. The purpose of this
 * interface is to expose an implementation of an InputManager.
 * An input manager factory implementation provides input managers for
 * a any number of supported specifications.</p>
 * 
 * <p>
 * To comply with this interface, an implementation must be thread safe and
 * address both the possibility that only a single instance is created and used
 * throughout and that new instances are created as desired.
 * </p>
 * 
 * @author Joel Håkansson
 */
public interface InputManagerFactory {
	
	/**
	 * Returns true if this factory can create instances for the specified locale.
	 * @param locale the locale to test
	 * @return true if this factory can create instances for the specified locale, false otherwise
	 */
	public boolean supportsSpecification(String locale, String fileFormat);
	
	/**
	 * Returns a new input manager configured for the specified locale.
	 * @param locale the locale for the new input manager
	 * @return returns a new input manager
	 */
	public InputManager newInputManager(String locale, String fileFormat);
	
	/**
	 * Lists the supported locales.
	 * @return returns a set of supported locales
	 */
	public Set<String> listSupportedLocales();

	/**
	 * Lists the supported file formats.
	 * @return returns a set of supported formats
	 */
	public Set<String> listSupportedFileFormats();
}
