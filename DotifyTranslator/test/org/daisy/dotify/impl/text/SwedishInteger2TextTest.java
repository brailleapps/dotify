package org.daisy.dotify.impl.text;

import static org.junit.Assert.assertEquals;

import org.daisy.dotify.text.IntegerOutOfRange;
import org.junit.Test;

public class SwedishInteger2TextTest {

	@Test
	public void testNumber01() throws IntegerOutOfRange {
		SwedishInteger2Text t = new SwedishInteger2Text();
		assertEquals("nittionio", t.intToText(99));
	}

	@Test
	public void testNumber02() throws IntegerOutOfRange {
		SwedishInteger2Text t = new SwedishInteger2Text();
		assertEquals("hundratrettiotvå", t.intToText(132));
	}

	@Test
	public void testNumber03() throws IntegerOutOfRange {
		SwedishInteger2Text t = new SwedishInteger2Text();
		assertEquals("ettusensjuhundrafemtioåtta", t.intToText(1758));
	}

	@Test
	public void testNumber04() throws IntegerOutOfRange {
		SwedishInteger2Text t = new SwedishInteger2Text();
		assertEquals("minus tolv", t.intToText(-12));
	}

	@Test
	public void testNumber05() throws IntegerOutOfRange {
		SwedishInteger2Text t = new SwedishInteger2Text();
		assertEquals("femton", t.intToText(15));
	}

}
