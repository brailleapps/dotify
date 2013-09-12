package org.daisy.dotify.text;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class Integer2TextFactoryTest {

	@Test
	public void testSwedishFactoryMaker() throws Integer2TextConfigurationException {
		assertNotNull(Integer2TextFactoryMaker.newInstance().newInteger2Text(FilterLocale.parse("sv-SE")));
	}

	@Test
	public void testEnglishFactoryMaker() throws Integer2TextConfigurationException {
		assertNotNull(Integer2TextFactoryMaker.newInstance().newInteger2Text(FilterLocale.parse("en")));
	}
}
