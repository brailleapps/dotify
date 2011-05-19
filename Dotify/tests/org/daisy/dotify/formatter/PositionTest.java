package org.daisy.dotify.formatter;
import static org.junit.Assert.assertEquals;

import org.daisy.dotify.formatter.Position;
import org.junit.Test;

public class PositionTest {

	@Test
	public void testParsePosition() {
		assertEquals(new Position(0.33d, true), Position.parsePosition("33%"));
		assertEquals(new Position(15d, false), Position.parsePosition("15"));
		assertEquals(new Position(0.2d, true), Position.parsePosition("20%"));
		assertEquals(new Position(0d, true), Position.parsePosition("0%"));
		assertEquals(new Position(1d, true), Position.parsePosition("100%"));
	}

	@Test
	public void testMakeAbsolute() {
		assertEquals(10, new Position(0.33d, true).makeAbsolute(30));
		assertEquals(15, new Position(15d, false).makeAbsolute(30));
		assertEquals(6, new Position(0.2d, true).makeAbsolute(28));
		assertEquals(0, new Position(0d, true).makeAbsolute(28));
		assertEquals(28, new Position(1d, true).makeAbsolute(28));
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNonIntegerAbsoluteValue() {
		new Position(1.1, false);
	}
	
	@Test (expected=IllegalArgumentException.class)
	public void testNegativeValue() {
		new Position(-0.1, false);
	}

}
