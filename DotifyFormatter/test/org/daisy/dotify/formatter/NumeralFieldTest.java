package org.daisy.dotify.formatter;

import static org.junit.Assert.assertEquals;

import org.daisy.dotify.formatter.NumeralField.NumeralStyle;
import org.junit.Test;

public class NumeralFieldTest {

	@Test
	public void testNumeralStyleDefault() {
		assertEquals("1", NumeralStyle.DEFAULT.format(1));
	}

	@Test
	public void testNumeralStyleRoman() {
		assertEquals("III", NumeralStyle.ROMAN.format(3));
	}

	@Test
	public void testNumeralStyleAlpha() {
		assertEquals("C", NumeralStyle.ALPHA.format(3));
	}
}
