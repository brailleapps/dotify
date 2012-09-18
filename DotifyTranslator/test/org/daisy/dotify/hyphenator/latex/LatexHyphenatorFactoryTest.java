package org.daisy.dotify.hyphenator.latex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.junit.Test;

public class LatexHyphenatorFactoryTest {

	@Test
	public void testEnglishHyphenator() throws UnsupportedLocaleException {
		FilterLocale locale = FilterLocale.parse("en");
		HyphenatorInterface h = new LatexHyphenatorFactory().newHyphenator(locale);
		
		//Test
		assertEquals("test­ing", h.hyphenate("testing"));
	}

	@Test
	public void testSupportedLocales(){
		boolean supports = true;
		//Test
		supports &= new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("en"));
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("en-US"));
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("en-GB"));
		
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("sv"));
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("sv-SE"));
		
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("no"));
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("no-NO"));
		
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("de"));
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("de-DE"));
		
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("fr"));
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("fr-FR"));
		
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("fi"));
		supports &=new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("fi-FI"));
		
		assertTrue(supports);
	}

	@Test
	public void testUnsupportedLocale() {
		//Test
		assertTrue(!new LatexHyphenatorFactory().supportsLocale(FilterLocale.parse("sv-SE-dummy")));
	}
	
}
