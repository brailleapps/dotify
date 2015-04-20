package org.daisy.dotify.consumer.translator;

import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.MarkerProcessor;
import org.daisy.dotify.api.translator.MarkerProcessorConfigurationException;
import org.daisy.dotify.consumer.translator.MarkerProcessorFactoryMaker;
import org.junit.Test;

public class MarkerProcessorFactoryTest {

	@Test
	public void testFactory() throws MarkerProcessorConfigurationException {
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor("en-US", BrailleTranslatorFactory.MODE_BYPASS);
		assertNotNull("Factory exists", m);
	}

	@Test
	public void testSwedishBrailleFactory() throws MarkerProcessorConfigurationException {
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		assertNotNull("Factory exists", m);
	}

	@Test
	public void testSwedishTextFactory() throws MarkerProcessorConfigurationException {
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor("sv-SE", BrailleTranslatorFactory.MODE_BYPASS);
		assertNotNull("Factory exists", m);
	}

}
