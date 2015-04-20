package org.daisy.dotify.consumer.translator;

import static org.junit.Assert.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TextBorderConfigurationException;
import org.daisy.dotify.api.translator.TextBorderFactory;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.junit.Test;

public class TextBorderFactoryTest {

	@Test(expected = TextBorderConfigurationException.class)
	public void testUnknownFactory() throws TextBorderConfigurationException {
		TextBorderFactoryMakerService maker = TextBorderFactoryMaker.newInstance();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(TextBorderFactory.FEATURE_MODE, BrailleTranslatorFactory.MODE_UNCONTRACTED);
		maker.newTextBorderStyle(props);
	}

	@Test
	public void testKnownFactory() throws TextBorderConfigurationException {
		TextBorderFactoryMakerService maker = TextBorderFactoryMaker.newInstance();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put(TextBorderFactory.FEATURE_MODE, BrailleTranslatorFactory.MODE_UNCONTRACTED);
		props.put("border-style", "solid");
		props.put("border-width", "1");
		props.put("border-align", "outer");
		assertNotNull("Implementation should exist", maker.newTextBorderStyle(props));
	}

}
