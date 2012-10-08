package org.daisy.dotify.formatter;

import static org.junit.Assert.assertTrue;

import org.junit.Test;


public class FormatterFactoryTest {

	@Test
	public void testFactory() {
		//setup
		Formatter f = FormatterFactory.newInstance().newFormatter();
		//test
		assertTrue("Assert that formatter can be instantiated", f!=null);
	}
}
