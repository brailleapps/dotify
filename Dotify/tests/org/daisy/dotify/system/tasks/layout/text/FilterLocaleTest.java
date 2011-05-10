package org.daisy.dotify.system.tasks.layout.text;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class FilterLocaleTest {
	
	@Test
	public void testIsA() {
		FilterLocale inLoc = FilterLocale.parse("sv-SE");
		FilterLocale refLoc = FilterLocale.parse("sv");
		assertTrue("Assert sv-SE is a sv", inLoc.isA(refLoc));
	}
	
	@Test
	public void testIsNotA_1() {
		FilterLocale inLoc = FilterLocale.parse("sv-FI");
		FilterLocale refLoc = FilterLocale.parse("da-DK");
		assertTrue("Assert sv-FI is not a da-DK", !inLoc.isA(refLoc));
	}
	
	@Test
	public void testIsNotA_2() {
		FilterLocale inLoc = FilterLocale.parse("sv-SE");
		FilterLocale refLoc = FilterLocale.parse("sv-SE-test");
		assertTrue("Assert sv-SE is not a sv-SE-test", !inLoc.isA(refLoc));
	}
	
	@Test
	public void testIsNotA_3() {
		FilterLocale inLoc = FilterLocale.parse("sv");
		FilterLocale refLoc = FilterLocale.parse("da");
		assertTrue("Assert sv is not a da", !inLoc.isA(refLoc));
	}

	@Test
	public void testSameIsA() {
		FilterLocale inLoc = FilterLocale.parse("sv-SE-test");
		FilterLocale refLoc = FilterLocale.parse("sv-SE-test");
		assertTrue("Assert sv-SE-test is a sv-SE-test", inLoc.isA(refLoc));
	}

}
