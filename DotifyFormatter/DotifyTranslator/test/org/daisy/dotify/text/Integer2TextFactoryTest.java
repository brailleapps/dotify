package org.daisy.dotify.text;

import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.junit.Test;

public class Integer2TextFactoryTest {

	@Test
	public void testSwedishFactoryMaker() throws UnsupportedLocaleException {
		assertNotNull(Integer2TextFactoryMaker.newInstance().newInteger2Text(FilterLocale.parse("sv-SE")));
	}
}
