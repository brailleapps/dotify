package org.daisy.dotify.translator.attributes;

import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.junit.Test;

public class MarkerProcessorFactoryTest {

	@Test
	public void testFactory() throws MarkerProcessorConfigurationException {
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor(FilterLocale.parse("en-US"), BrailleTranslatorFactory.MODE_BYPASS);
		assertNotNull("Factory exists", m);
	}

	@Test
	public void testSwedishBrailleFactory() throws MarkerProcessorConfigurationException {
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor(FilterLocale.parse("sv-SE"), BrailleTranslatorFactory.MODE_UNCONTRACTED);
		assertNotNull("Factory exists", m);
	}

	@Test
	public void testSwedishTextFactory() throws MarkerProcessorConfigurationException {
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor(FilterLocale.parse("sv-SE"), BrailleTranslatorFactory.MODE_BYPASS);
		assertNotNull("Factory exists", m);
	}

}
