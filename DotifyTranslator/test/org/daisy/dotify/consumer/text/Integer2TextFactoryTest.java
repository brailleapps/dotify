package org.daisy.dotify.consumer.text;

import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.junit.Test;

public class Integer2TextFactoryTest {

	@Test
	public void testSwedishFactoryMaker() throws Integer2TextConfigurationException {
		assertNotNull(Integer2TextFactoryMaker.newInstance().newInteger2Text("sv-SE"));
	}

	@Test
	public void testEnglishFactoryMaker() throws Integer2TextConfigurationException {
		assertNotNull(Integer2TextFactoryMaker.newInstance().newInteger2Text("en"));
	}
}
