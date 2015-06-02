package spi;

import static org.junit.Assert.assertNotNull;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.consumer.formatter.FormatterFactoryMaker;
import org.junit.Test;


public class FormatterFactoryTest {
	
	@Test
	public void testFactory() {
		//Setup
		FormatterFactoryMaker ff = FormatterFactoryMaker.newInstance();
		//Test
		assertNotNull(ff);
	}

	@Test
	public void testSwedishFormatter() {
		//setup
		Formatter f = FormatterFactoryMaker.newInstance().newFormatter("sv-SE", BrailleTranslatorFactory.MODE_UNCONTRACTED);
		//test
		assertNotNull("Assert that formatter can be instantiated", f);
	}
}
