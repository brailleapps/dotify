package org.daisy.dotify.impl.translator;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a Braille translator for javascript wrapping. It is similar
 * to BrailleTranslator, but returns Object instead of BrailleTranslatorResult.
 * Note that the object returned should still implement the methods of 
 * BrailleTranslatorResult.
 *  
 * @author Joel Håkansson
 */
public interface JavascriptBrailleTranslator {

	/**
	 * Translates the string in the specified language.
	 * @param text the text to translate
	 * @param locale the language/region of the text
	 * @return returns the translator result
	 * @throws UnsupportedLocaleException if the locale is not supported by the implementation
	 */
	public Object translate(String text, FilterLocale locale) throws UnsupportedLocaleException;
	
	/**
	 * Translate the string using the translator's default language.
	 * @param text
	 * @return returns the translator result
	 */
	public Object translate(String text);
	
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
