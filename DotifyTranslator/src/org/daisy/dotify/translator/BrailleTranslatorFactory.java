package org.daisy.dotify.translator;

import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a factory for braille translation and hyphenation.
 * @author Joel Håkansson
 *
 */
public interface BrailleTranslatorFactory {
	public final static String MODE_BYPASS = "bypass";
	public final static String MODE_UNCONTRACTED = "uncontracted";
	
	/**
	 * Returns true if the translator factory supports the given specification.
	 * @param locale the translator locale
	 * @param mode the translator grade, or null for uncontracted braille
	 * @return returns true if the translator factory supports the specification
	 */
	public boolean supportsSpecification(FilterLocale locale, String mode);
	
	/**
	 * Creates a new translator with the given specification
	 * @param locale the translator locale
	 * @param mode the translator grade, or null for uncontracted braille
	 * @return returns a new translator
	 * @throws UnsupportedSpecificationException if the translator factory does not support
	 * the desired specification
	 */
	public BrailleTranslator newTranslator(FilterLocale locale, String mode) throws UnsupportedSpecificationException;

}
