package org.daisy.dotify.devtools.converters;

import org.daisy.dotify.devtools.converters.CodePointHelper.Mode;
import org.daisy.dotify.devtools.converters.CodePointHelper.Style;
import org.junit.Test;

public class CodePointHelperTest {

	@Test
	public void testToHexString_1() {
		String expected = "0001";
		String actual = CodePointHelper.toHexString(1, 4);
		org.junit.Assert.assertEquals(expected, actual);
	}

	@Test
	public void testToHexString_2() {
		String expected = "0010";
		String actual =  CodePointHelper.toHexString(16, 4);
		org.junit.Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testParse() {
		String input = "0065,0066,0067";
		String expected =  "ABC";
		String actual = CodePointHelper.parse(input, Mode.DECIMAL);
		org.junit.Assert.assertEquals(expected, actual);
		
	}
	
	@Test
	public void testFormat() {
		String input = "ABC";
		String expected = "65, 66, 67";
		String actual = CodePointHelper.format(input, Style.COMMA, Mode.DECIMAL);
		org.junit.Assert.assertEquals(expected, actual);
	}


}
