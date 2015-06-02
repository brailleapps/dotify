package spi;

import static org.junit.Assert.*;

import org.daisy.dotify.api.text.Integer2Text;
import org.daisy.dotify.api.text.Integer2TextConfigurationException;
import org.daisy.dotify.api.text.IntegerOutOfRange;
import org.daisy.dotify.consumer.text.Integer2TextFactoryMaker;
import org.junit.Test;

public class Integer2TextFactoryTest {
	
	@Test
	public void testInt2TextFactory() {
		//Setup
		Integer2TextFactoryMaker int2textFactory = Integer2TextFactoryMaker.newInstance();
		//Test
		assertNotNull(int2textFactory);
		assertTrue(int2textFactory.listLocales().size()>=3);
	}

	@Test
	public void testSwedishInt2Text() throws Integer2TextConfigurationException {
		//Setup
		Integer2Text sv = Integer2TextFactoryMaker.newInstance().newInteger2Text("sv-SE");
		//Test
		assertNotNull(sv);
	}

	@Test
	public void testEnglishInt2Text() throws Integer2TextConfigurationException, IntegerOutOfRange {
		//Setup
		Integer2Text en = Integer2TextFactoryMaker.newInstance().newInteger2Text("en");
		//Test
		assertNotNull(en);
		assertEquals("two", en.intToText(2));
	}
}