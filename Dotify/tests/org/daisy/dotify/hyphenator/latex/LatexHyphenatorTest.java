package org.daisy.dotify.hyphenator.latex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.junit.Test;

public class LatexHyphenatorTest {

	@Test
	public void testEnglishHyphenator() throws UnsupportedLocaleException {
		FilterLocale locale = FilterLocale.parse("en");
		HyphenatorInterface h = new LatexHyphenator().newHyphenator(locale);
		
		//Test
		assertEquals("test­ing", h.hyphenate("testing"));
	}

	@Test
	public void testSupportedLocales(){
		
		//Test
		try {
			new LatexHyphenator().newHyphenator(FilterLocale.parse("en"));
			new LatexHyphenator().newHyphenator(FilterLocale.parse("en-US"));
			new LatexHyphenator().newHyphenator(FilterLocale.parse("en-GB"));
			
			new LatexHyphenator().newHyphenator(FilterLocale.parse("sv"));
			new LatexHyphenator().newHyphenator(FilterLocale.parse("sv-SE"));
			
			new LatexHyphenator().newHyphenator(FilterLocale.parse("no"));
			new LatexHyphenator().newHyphenator(FilterLocale.parse("no-NO"));
			
			new LatexHyphenator().newHyphenator(FilterLocale.parse("de"));
			new LatexHyphenator().newHyphenator(FilterLocale.parse("de-DE"));
			
			new LatexHyphenator().newHyphenator(FilterLocale.parse("fr"));
			new LatexHyphenator().newHyphenator( FilterLocale.parse("fr-FR"));
			
			new LatexHyphenator().newHyphenator(FilterLocale.parse("fi"));
			new LatexHyphenator().newHyphenator(FilterLocale.parse("fi-FI"));
		} catch (UnsupportedLocaleException e) {
			//if locale is not supported, assertation failed
			assertTrue(false);
		}
	}
	
	@Test (expected=UnsupportedLocaleException.class)
	public void testUnsupportedLocale() throws UnsupportedLocaleException {
		//Test
		new LatexHyphenator().newHyphenator(FilterLocale.parse("sv-SE-dummy"));
	}
	
}
