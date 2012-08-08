package org.daisy.dotify.translator;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.text.FilterLocale;
import org.junit.Test;

public class BrailleFilterFactoryTest {

	@Test
	public void testFactoryExists() {
		//Setup
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testLocateFilterForSwedish() {
		//Setup
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		FilterLocale locale = FilterLocale.parse("sv-se");

		//Test
		assertTrue(factory.newStringFilter(locale)!=null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testLocateUnknownFilter() {
		//Setup
		BrailleFilterFactory factory = BrailleFilterFactory.newInstance();
		FilterLocale locale = FilterLocale.parse("fi");

		//Test
		assertTrue(factory.newStringFilter(locale)!=null);
	}


}
