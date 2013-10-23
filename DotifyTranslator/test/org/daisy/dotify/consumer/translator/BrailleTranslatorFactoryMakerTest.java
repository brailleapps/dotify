package org.daisy.dotify.consumer.translator;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.junit.Test;

public class BrailleTranslatorFactoryMakerTest {

	@Test
	public void testFactoryExists() {
		//Setup
		BrailleTranslatorFactoryMaker factory = BrailleTranslatorFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testImplementationForSwedishExists() throws TranslatorConfigurationException {
		//Setup
		BrailleTranslatorFactoryMaker factory = BrailleTranslatorFactoryMaker.newInstance();
		
		//Test
		assertTrue(factory.newTranslator("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED) != null);
	}

	@Test
	public void testImplementationForEnglishExists() throws TranslatorConfigurationException {
		// Setup
		BrailleTranslatorFactoryMaker factory = BrailleTranslatorFactoryMaker.newInstance();

		// Test
		assertTrue(factory.newTranslator("en", BrailleTranslatorFactory.MODE_BYPASS) != null);
	}
}
