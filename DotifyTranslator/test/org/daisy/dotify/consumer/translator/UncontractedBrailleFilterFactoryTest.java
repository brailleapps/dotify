package org.daisy.dotify.consumer.translator;

import static org.junit.Assert.assertTrue;

import org.daisy.dotify.consumer.translator.UncontractedBrailleFilterFactory;
import org.junit.Test;

public class UncontractedBrailleFilterFactoryTest {

	@Test
	public void testFactoryExists() {
		//Setup
		UncontractedBrailleFilterFactory factory = UncontractedBrailleFilterFactory.newInstance();
		//Test
		assertTrue(factory != null);
	}
	
	@Test
	public void testLocateFilterForSwedish() {
		//Setup
		UncontractedBrailleFilterFactory factory = UncontractedBrailleFilterFactory.newInstance();

		//Test
		assertTrue(factory.newStringFilter("sv-se") != null);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testLocateUnknownFilter() {
		//Setup
		UncontractedBrailleFilterFactory factory = UncontractedBrailleFilterFactory.newInstance();

		//Test
		assertTrue(factory.newStringFilter("fi") != null);
	}


}
