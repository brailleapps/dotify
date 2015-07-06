package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.junit.Test;

public abstract class HyphenatorFactoryMakerTestbase {
	
	public abstract HyphenatorFactoryMakerService getHyphenatorFMS();

	@Test
	public void testHyphenatorFactory() {
		//Setup
		HyphenatorFactoryMakerService hyphenatorFactory = getHyphenatorFMS();
		//Test
		assertNotNull(hyphenatorFactory);
		assertTrue(hyphenatorFactory.listLocales().size()>=61);
	}
	
	@Test
	public void testEnglishHyphenator() throws HyphenatorConfigurationException {
		//Setup
		HyphenatorFactoryMakerService factory = getHyphenatorFMS();
		HyphenatorInterface h = factory.newHyphenator("en");
		//Test
		assertNotNull(h);
		assertEquals("hy­phen­a­tion", h.hyphenate("hyphenation"));
	}
}
