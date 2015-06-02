package spi;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.daisy.dotify.consumer.hyphenator.HyphenatorFactoryMaker;
import org.junit.Test;

public class HyphenatorFactoryMakerTest {

	@Test
	public void testHyphenatorFactory() {
		//Setup
		HyphenatorFactoryMakerService hyphenatorFactory = HyphenatorFactoryMaker.newInstance();
		//Test
		assertNotNull(hyphenatorFactory);
		assertTrue(hyphenatorFactory.listLocales().size()>=61);
	}
	
	@Test
	public void testEnglishHyphenator() throws HyphenatorConfigurationException {
		//Setup
		HyphenatorFactoryMakerService factory = HyphenatorFactoryMaker.newInstance();
		HyphenatorInterface h = factory.newHyphenator("en");
		//Test
		assertNotNull(h);
		assertEquals("hy­phen­a­tion", h.hyphenate("hyphenation"));
	}
}
