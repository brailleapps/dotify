package org.daisy.dotify.system.tasks.layout.utils;
import static org.junit.Assert.assertEquals;

import org.daisy.dotify.formatter.utils.Expression;
import org.junit.Test;


public class ExpressionTest {
	
	@Test
	public void testExpression() {
		Expression e = new Expression();
		assertEquals(10d, e.evaluate("(+ 7 3)"));
		assertEquals(15d, e.evaluate("(+ 7 3) (+ 4 11)"));
		assertEquals(24d, e.evaluate("(* 4 (+ 1 1 1) 2)"));
		assertEquals(1d, e.evaluate("( % (+ (* 12  2) 1) 2)"));
		assertEquals(10d, e.evaluate("(/ 50 5)"));
		assertEquals(false, e.evaluate("(= 50 5)"));
		assertEquals(true, e.evaluate("(= 5.000d 5f 5)"));
		assertEquals(false, e.evaluate("(= 5 5 5 1)"));
		assertEquals(true, e.evaluate("(< 5 6 7)"));
		assertEquals(false, e.evaluate("(< 100 6)"));
		assertEquals(false, e.evaluate("(< 6 6)"));
		assertEquals(true, e.evaluate("(<= 6 6)"));
		assertEquals(false, e.evaluate("(> 6 6)"));
		assertEquals(true, e.evaluate("(>= 6 6)"));
		assertEquals(true, e.evaluate("(& (= 1 1) (= 2 2))"));
		assertEquals(true, e.evaluate("(| (= 1 0) (= 2 2))"));
		assertEquals(false, e.evaluate("(& (= 1 1) (= 1 2))"));
		assertEquals(false, e.evaluate("(| (= 1 0) (= 2 1))"));
		assertEquals(17d, e.evaluate("(+ (if (= 1 0) 18 17) 0)"));
		assertEquals(18d, e.evaluate("(if (< 1 3) 18 17)"));
		assertEquals("2011", e.evaluate("(now \"yyyy\")"));// stupid test
		assertEquals(36, e.evaluate("(set var 3) (set var1 12) (round (* var var1))"));
	}

	/*
		input + " -> " + ret + " (" +ret.getClass() + ")";
	 */

}
