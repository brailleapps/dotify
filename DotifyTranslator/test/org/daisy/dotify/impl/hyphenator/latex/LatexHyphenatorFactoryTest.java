package org.daisy.dotify.impl.hyphenator.latex;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.daisy.dotify.text.FilterLocale;
import org.junit.Test;

public class LatexHyphenatorFactoryTest {

	@Test
	public void testEnglishHyphenator() throws HyphenatorConfigurationException {
		FilterLocale locale = FilterLocale.parse("en");
		HyphenatorInterface h = new LatexHyphenatorFactory().newHyphenator(locale.toString());
		
		//Test
		assertEquals("test­ing", h.hyphenate("testing"));
	}

	@Test
	public void testSupportedLocales(){
		boolean supports = true;
		//Test
		supports &= new LatexHyphenatorFactory().supportsLocale("en");
		supports &= new LatexHyphenatorFactory().supportsLocale("en-US");
		supports &= new LatexHyphenatorFactory().supportsLocale("en-GB");
		
		supports &= new LatexHyphenatorFactory().supportsLocale("sv");
		supports &= new LatexHyphenatorFactory().supportsLocale("sv-SE");
		
		supports &= new LatexHyphenatorFactory().supportsLocale("no");
		supports &= new LatexHyphenatorFactory().supportsLocale("no-NO");
		
		supports &= new LatexHyphenatorFactory().supportsLocale("de");
		supports &= new LatexHyphenatorFactory().supportsLocale("de-DE");
		
		supports &= new LatexHyphenatorFactory().supportsLocale("fr");
		supports &= new LatexHyphenatorFactory().supportsLocale("fr-FR");
		
		supports &= new LatexHyphenatorFactory().supportsLocale("fi");
		supports &= new LatexHyphenatorFactory().supportsLocale("fi-FI");
		
		assertTrue(supports);
	}

	@Test
	public void testUnsupportedLocale() {
		//Test
		assertTrue(!new LatexHyphenatorFactory().supportsLocale("sv-SE-dummy"));
	}
	
}
