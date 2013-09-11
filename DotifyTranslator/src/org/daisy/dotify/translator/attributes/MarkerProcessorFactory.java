package org.daisy.dotify.translator.attributes;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.TranslatorConfigurationException;

/**
 * Provides a factory for braille markers.
 * 
 * @author Joel Håkansson
 * 
 */
public interface MarkerProcessorFactory {

	/**
	 * Returns true if the marker processor factory supports the given specification.
	 * @param locale the marker processor locale
	 * @param mode the marker processor grade
	 * @return returns true if the marker processor factory supports the specification
	 */
	public boolean supportsSpecification(FilterLocale locale, String mode);
	
	/**
	 * Creates a new marker processor with the given specification
	 * @param locale the marker processor locale
	 * @param mode the marker processor grade
	 * @return returns a new marker processor
	 * @throws TranslatorConfigurationException if the specification is not supported
	 */
	public MarkerProcessor newMarkerProcessor(FilterLocale locale, String mode) throws MarkerProcessorConfigurationException;

}
