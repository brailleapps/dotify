package org.daisy.dotify.impl.hyphenator.latex;

import static org.junit.Assert.assertEquals;

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
	
}
