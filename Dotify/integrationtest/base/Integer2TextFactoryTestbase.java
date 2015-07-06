package base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.daisy.dotify.api.text.Integer2Text;
import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.daisy.dotify.api.text.Integer2TextFactoryMakerService;
import org.daisy.dotify.api.text.IntegerOutOfRange;
import org.junit.Test;

public abstract class Integer2TextFactoryTestbase {
	
	public abstract Integer2TextFactoryMakerService getInteger2TextFMS();
	
	@Test
	public void testInt2TextFactory() {
		//Setup
		Integer2TextFactoryMakerService int2textFactory = getInteger2TextFMS();
		//Test
		assertNotNull(int2textFactory);
		assertTrue(int2textFactory.listLocales().size()>=3);
	}

	@Test
	public void testSwedishInt2Text() throws Integer2TextConfigurationException {
		//Setup
		Integer2Text sv = getInteger2TextFMS().newInteger2Text("sv-SE");
		//Test
		assertNotNull(sv);
	}

	@Test
	public void testEnglishInt2Text() throws Integer2TextConfigurationException, IntegerOutOfRange {
		//Setup
		Integer2Text en = getInteger2TextFMS().newInteger2Text("en");
		//Test
		assertNotNull(en);
		assertEquals("two", en.intToText(2));
	}
}