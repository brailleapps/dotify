package org.daisy.dotify.devtools.converters;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class UnicodeNamesTest {

	@Test
	public void test_01() {
		assertEquals("SPACE", UnicodeNames.getName(32));
	}
	
	@Test
	public void test_02() {
		assertEquals("LATIN CAPITAL LETTER A", UnicodeNames.getName(65));
	}
	
	@Test
	public void test_03() {
		assertEquals("LATIN CAPITAL LETTER C", UnicodeNames.getName(67));
	}

}
