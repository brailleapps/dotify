package org.daisy.dotify.hyphenator;

import static org.junit.Assert.*;

import org.daisy.dotify.text.FilterLocale;
import org.junit.*;

public class HyphenatorFactoryTest {

	@Test
	public void testFactoryExists() {
		//Setup
		HyphenatorFactory factory = HyphenatorFactory.newInstance();
		
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testImplementationForEnglishExists() {
		//Setup
		HyphenatorFactory factory = HyphenatorFactory.newInstance();
		FilterLocale filter = FilterLocale.parse("en");
		
		//Test
		assertTrue(factory.getHyphenator(filter)!=null);
	}
}
