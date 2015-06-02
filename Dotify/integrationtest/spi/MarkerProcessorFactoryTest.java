package spi;

import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.MarkerProcessor;
import org.daisy.dotify.api.translator.MarkerProcessorConfigurationException;
import org.daisy.dotify.api.translator.MarkerProcessorFactoryMakerService;
import org.daisy.dotify.consumer.translator.MarkerProcessorFactoryMaker;
import org.junit.Test;

public class MarkerProcessorFactoryTest {
	
	@Test
	public void testMarkerProcessorFactory() {
		//Setup
		MarkerProcessorFactoryMakerService m =  MarkerProcessorFactoryMaker.newInstance();
		//Test
		assertNotNull("Factory exists", m);
	}

	@Test
	public void testEnglishBypassMarkerProcessor() throws MarkerProcessorConfigurationException {
		//Setup
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor("en-US", BrailleTranslatorFactory.MODE_BYPASS);
		//Test
		assertNotNull("Factory exists", m);
	}

	@Test
	public void testSwedishBrailleMarkerProcessor() throws MarkerProcessorConfigurationException {
		//Setup
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		//Test
		assertNotNull("Factory exists", m);
	}

	@Test
	public void testSwedishTextFactoryMarkerProcessor() throws MarkerProcessorConfigurationException {
		//Setup
		MarkerProcessor m = MarkerProcessorFactoryMaker.newInstance().newMarkerProcessor("sv-SE", BrailleTranslatorFactory.MODE_BYPASS);
		//Test
		assertNotNull("Factory exists", m);
	}

}