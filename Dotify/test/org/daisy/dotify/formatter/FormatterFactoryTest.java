package org.daisy.dotify.formatter;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.text.FilterLocale;
import org.junit.Test;


public class FormatterFactoryTest {

	@Test
	public void testFactory() {
		//setup
		Formatter f = FormatterFactoryMaker.newInstance().newFormatter(FilterLocale.parse("sv-SE"), BrailleTranslatorFactory.MODE_UNCONTRACTED);
		//test
		assertTrue("Assert that formatter can be instantiated", f!=null);
	}
}
