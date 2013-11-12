package org.daisy.dotify.formatter.impl;

import static org.junit.Assert.assertEquals;

import org.daisy.dotify.api.formatter.NumeralField.NumeralStyle;
import org.junit.Test;

public class NumeralStyleFormatterTest {

	@Test
	public void testNumeralStyleDefault() {
		assertEquals("1", NumeralStyleFormatter.format(1, NumeralStyle.DEFAULT));
	}

	@Test
	public void testNumeralStyleRoman() {
		assertEquals("III", NumeralStyleFormatter.format(3, NumeralStyle.ROMAN));
	}

	@Test
	public void testNumeralStyleAlpha() {
		assertEquals("C", NumeralStyleFormatter.format(3, NumeralStyle.ALPHA));
	}
}
