package org.daisy.dotify.setups;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.setups.DefaultInputManagerFactory;
import org.daisy.dotify.text.FilterLocale;
import org.junit.Test;

public class DefaultInputManagerFactoryTest {

	@Test
	public void testSupportsLocale() {
		//Setup
		DefaultInputManagerFactory factory = new DefaultInputManagerFactory();
		
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testLocateInputManagerForEnglish() throws UnsupportedLocaleException {
		//Setup
		DefaultInputManagerFactory factory = new DefaultInputManagerFactory();
		FilterLocale filter = FilterLocale.parse("en-US");
		
		//Test
		assertTrue(factory.newInputManager(filter)!=null);
	}

	
	@Test
	public void testLocateInputManagerForSwedish() throws UnsupportedLocaleException {
		//Setup
		DefaultInputManagerFactory factory = new DefaultInputManagerFactory();
		FilterLocale filter = FilterLocale.parse("sv-SE");
		
		//Test
		assertTrue(factory.newInputManager(filter)!=null);
	}
	/*
	@Test
	public void testLocateInputManagerForSwedishFA44() throws UnsupportedLocaleException {
		//Setup
		DefaultInputManagerFactory factory = new DefaultInputManagerFactory();
		FilterLocale filter = FilterLocale.parse("sv-SE-FA44");
		
		//Test
		assertTrue(factory.newInputManager(filter)!=null);
	}
	*/
	@Test (expected=IllegalArgumentException.class)
	public void testLocateInputManagerForUnknownLocale() throws UnsupportedLocaleException {
		//Setup
		DefaultInputManagerFactory factory = new DefaultInputManagerFactory();
		FilterLocale filter = FilterLocale.parse("fi");
		
		//Test
		assertTrue(factory.newInputManager(filter)!=null);
	}
}
