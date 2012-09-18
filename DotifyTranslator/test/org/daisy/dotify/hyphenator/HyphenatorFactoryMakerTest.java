package org.daisy.dotify.hyphenator;

import static org.junit.Assert.*;

import org.daisy.dotify.text.FilterLocale;
import org.junit.*;

public class HyphenatorFactoryMakerTest {

	@Test
	public void testFactoryExists() {
		//Setup
		HyphenatorFactoryMaker factory = HyphenatorFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testImplementationForEnglishExists() throws UnsupportedLocaleException {
		//Setup
		HyphenatorFactoryMaker factory = HyphenatorFactoryMaker.newInstance();
		FilterLocale filter = FilterLocale.parse("en");
		
		//Test
		assertTrue(factory.newHyphenator(filter)!=null);
	}
}
