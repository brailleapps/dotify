package org.daisy.dotify.consumer.hyphenator;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.junit.Test;

public class HyphenatorFactoryMakerTest {

	@Test
	public void testFactoryExists() {
		//Setup
		HyphenatorFactoryMakerService factory = HyphenatorFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testImplementationForEnglishExists() throws HyphenatorConfigurationException {
		//Setup
		HyphenatorFactoryMakerService factory = HyphenatorFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory.newHyphenator("en") != null);
	}
}
