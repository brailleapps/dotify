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
		HyphenatorInterface h = new LatexHyphenator().newInstance(locale);
		
		//Test
		assertEquals("test­ing", h.hyphenate("testing"));
	}

	@Test
	public void testSupportedLocales(){
		
		//Test
		try {
			new LatexHyphenator().newInstance(FilterLocale.parse("en"));
			new LatexHyphenator().newInstance(FilterLocale.parse("en-US"));
			new LatexHyphenator().newInstance(FilterLocale.parse("en-GB"));
			
			new LatexHyphenator().newInstance(FilterLocale.parse("sv"));
			new LatexHyphenator().newInstance(FilterLocale.parse("sv-SE"));
			
			new LatexHyphenator().newInstance(FilterLocale.parse("no"));
			new LatexHyphenator().newInstance(FilterLocale.parse("no-NO"));
			
			new LatexHyphenator().newInstance(FilterLocale.parse("de"));
			new LatexHyphenator().newInstance(FilterLocale.parse("de-DE"));
			
			new LatexHyphenator().newInstance(FilterLocale.parse("fr"));
			new LatexHyphenator().newInstance( FilterLocale.parse("fr-FR"));
			
			new LatexHyphenator().newInstance(FilterLocale.parse("fi"));
			new LatexHyphenator().newInstance(FilterLocale.parse("fi-FI"));
		} catch (UnsupportedLocaleException e) {
			//if locale is not supported, assertation failed
			assertTrue(false);
		}
	}
	
	@Test (expected=UnsupportedLocaleException.class)
	public void testUnsupportedLocale() throws UnsupportedLocaleException {
		//Test
		new LatexHyphenator().newInstance(FilterLocale.parse("sv-SE-dummy"));
	}
	
}
