package org.daisy.dotify.translator;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides an interface for braille translation and hyphenation for a particular
 * locale. The locale is determined when the translator is instantiated
 * by the factory. 
 * @author Joel Håkansson
 */
public interface BrailleTranslator {
	
	/**
	 * Translates the string in the specified language.
	 * @param text the text to translate
	 * @param locale the language/region of the text
	 * @return returns the translator result
	 * @throws UnsupportedLocaleException if the locale is not supported by the implementation
	 */
	public BrailleTranslatorResult translate(String text, FilterLocale locale) throws UnsupportedLocaleException;
	
	/**
	 * Translate the string using the translator's default language.
	 * @param text
	 * @param limit
	 * @return
	 */
	public BrailleTranslatorResult translate(String text);
	
	/**
	 * Sets hyphenating to the specified value.  Setting hyphenating to false indicates 
	 * that hyphenation should not be performed.
	 */
	public void setHyphenating(boolean value);
	
	/**
	 * Returns true if the translator is hyphenating.
	 * @return returns true if the translator is hyphenating, false otherwise.
	 */
	public boolean isHyphenating();
	
}
