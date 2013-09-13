package org.daisy.dotify.impl.text;

import static org.junit.Assert.assertEquals;

import org.daisy.dotify.api.text.IntegerOutOfRange;
import org.junit.Test;

public class BasicInteger2TextTest {
	private BasicInteger2Text sv = new BasicInteger2Text(new SvInt2TextLocalization());
	private BasicInteger2Text en = new BasicInteger2Text(new EnInt2TextLocalization());

	@Test
	public void testNumber_sv_01() throws IntegerOutOfRange {
		assertEquals("nittionio", sv.intToText(99));
	}

	@Test
	public void testNumber_sv_02() throws IntegerOutOfRange {
		assertEquals("etthundratrettiotvå", sv.intToText(132));
	}

	@Test
	public void testNumber_sv_03() throws IntegerOutOfRange {
		assertEquals("ettusensjuhundrafemtioåtta", sv.intToText(1758));
	}

	@Test
	public void testNumber_sv_04() throws IntegerOutOfRange {
		assertEquals("minus tolv", sv.intToText(-12));
	}

	@Test
	public void testNumber_sv_05() throws IntegerOutOfRange {
		assertEquals("femton", sv.intToText(15));
	}

	@Test
	public void testNumber_sv_06() throws IntegerOutOfRange {
		assertEquals("tvåhundratrettiotvå", sv.intToText(232));
	}

	@Test
	public void testNumber_sv_07() throws IntegerOutOfRange {
		assertEquals("fem", sv.intToText(5));
	}

	@Test
	public void testNumber_sv_08() throws IntegerOutOfRange {
		assertEquals("tjugoåtta", sv.intToText(28));
	}

	@Test
	public void testNumber_sv_09() throws IntegerOutOfRange {
		assertEquals("etthundra", sv.intToText(100));
	}

	@Test
	public void testNumber_sv_10() throws IntegerOutOfRange {
		assertEquals("ettusen", sv.intToText(1000));
	}


	@Test
	public void testNumber_en_01() throws IntegerOutOfRange {
		assertEquals("ninety-nine", en.intToText(99));
	}

	@Test
	public void testNumber_en_02() throws IntegerOutOfRange {
		assertEquals("one hundred thirty-two", en.intToText(132));
	}

	@Test
	public void testNumber_en_03() throws IntegerOutOfRange {
		assertEquals("one thousand seven hundred fifty-eight", en.intToText(1758));
	}

	@Test
	public void testNumber_en_04() throws IntegerOutOfRange {
		assertEquals("minus twelve", en.intToText(-12));
	}

	@Test
	public void testNumber_en_05() throws IntegerOutOfRange {
		assertEquals("fifteen", en.intToText(15));
	}

	@Test
	public void testNumber_en_06() throws IntegerOutOfRange {
		assertEquals("two hundred thirty-two", en.intToText(232));
	}

	@Test
	public void testNumber_en_07() throws IntegerOutOfRange {
		assertEquals("five", en.intToText(5));
	}

	@Test
	public void testNumber_en_08() throws IntegerOutOfRange {
		assertEquals("twenty-eight", en.intToText(28));
	}

	@Test
	public void testNumber_en_09() throws IntegerOutOfRange {
		assertEquals("one hundred", en.intToText(100));
	}

	@Test
	public void testNumber_en_10() throws IntegerOutOfRange {
		assertEquals("one thousand", en.intToText(1000));
	}

}
