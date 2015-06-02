package spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.consumer.translator.BrailleTranslatorFactoryMaker;
import org.junit.Test;

public class BrailleTranslatorFactoryMakerTest {

	@Test
	public void testTranslatorFactory() {
		//Setup
		BrailleTranslatorFactoryMaker translatorFactory = BrailleTranslatorFactoryMaker.newInstance();
		//Test
		assertNotNull("Factory exists.", translatorFactory);
		assertTrue(translatorFactory.listSpecifications().size()>=62);
	}
	
	@Test
	public void testSwedishUncontractedTranslator() throws TranslatorConfigurationException {
		//Setup
		BrailleTranslatorFactoryMaker translatorFactory = BrailleTranslatorFactoryMaker.newInstance();
		BrailleTranslator bt = translatorFactory.newTranslator("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		//Test
		assertNotNull(bt);
		assertEquals("⠼⠁⠃⠉", bt.translate("123").getTranslatedRemainder());
	}

	@Test
	public void testEnglishBypassTranslator() throws TranslatorConfigurationException {
		// Setup
		BrailleTranslatorFactoryMaker translatorFactory = BrailleTranslatorFactoryMaker.newInstance();
		BrailleTranslator bt = translatorFactory.newTranslator("en", BrailleTranslatorFactory.MODE_BYPASS);
		// Test
		assertNotNull(bt);
		assertEquals("123", bt.translate("123").getTranslatedRemainder());
	}
}
