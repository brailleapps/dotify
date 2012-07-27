package org.daisy.dotify.hyphenator.latex;

import static org.junit.Assert.*;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.junit.*;

public class LatexHyphenatorTest {

	@Test
	public void testEnglishHyphenator() throws UnsupportedLocaleException {
		FilterLocale locale = FilterLocale.parse("en");
		LatexHyphenator h = new LatexHyphenator();
		
		//Test
		assertEquals("test­ing", h.hyphenate("testing", locale, 0, 0));
	}

	@Test
	public void testSupportedLocales(){
		LatexHyphenator h = new LatexHyphenator();
		
		//Test
		try {
			h.hyphenate("testing", FilterLocale.parse("en"), 0, 0);
			h.hyphenate("testing", FilterLocale.parse("en-US"), 0, 0);
			h.hyphenate("testing", FilterLocale.parse("en-GB"), 0, 0);
			
			h.hyphenate("testing", FilterLocale.parse("sv"), 0, 0);
			h.hyphenate("testing", FilterLocale.parse("sv-SE"), 0, 0);
			
			h.hyphenate("testing", FilterLocale.parse("no"), 0, 0);
			h.hyphenate("testing", FilterLocale.parse("no-NO"), 0, 0);
			
			h.hyphenate("testing", FilterLocale.parse("de"), 0, 0);
			h.hyphenate("testing", FilterLocale.parse("de-DE"), 0, 0);
			
			h.hyphenate("testing", FilterLocale.parse("fr"), 0, 0);
			h.hyphenate("testing", FilterLocale.parse("fr-FR"), 0, 0);
			
			h.hyphenate("testing", FilterLocale.parse("fi"), 0, 0);
			h.hyphenate("testing", FilterLocale.parse("fi-FI"), 0, 0);
		} catch (UnsupportedLocaleException e) {
			//if locale is not supported, assertation failed
			assertTrue(false);
		}
	}
	
	@Test (expected=UnsupportedLocaleException.class)
	public void testUnsupportedLocale() throws UnsupportedLocaleException {
		LatexHyphenator h = new LatexHyphenator();
		FilterLocale locale = FilterLocale.parse("sv-SE-dummy");
		//Test
		h.hyphenate("testing", locale, 0, 0);
	}
	
}
