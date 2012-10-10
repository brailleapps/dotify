package org.daisy.dotify.obfl;
import static org.junit.Assert.assertEquals;

import org.daisy.dotify.obfl.Expression;
import org.junit.Test;


public class ExpressionTest {
	private final Expression e = new Expression();
	
	@Test
	public void testExpression_add_01() {
		assertEquals(10d, e.evaluate("(+ 7 3)"));
	}
	@Test
	public void testExpression_add_02() {
		assertEquals(15d, e.evaluate("(+ 7 3) (+ 4 11)"));
	}
	@Test
	public void testExpression_add_03() {
		assertEquals(24d, e.evaluate("(* 4 (+ 1 1 1) 2)"));
	}
	@Test
	public void testExpression_add_04() {
		assertEquals(1d, e.evaluate("( % (+ (* 12  2) 1) 2)"));
	}
	@Test
	public void testExpression_divide_01() {
		assertEquals(10d, e.evaluate("(/ 50 5)"));
	}
	@Test
	public void testExpression_divide_02() {
		assertEquals(1d, e.evaluate("(/ 20 5 4)"));
	}
	@Test
	public void testExpression_divide_03() {
		assertEquals(0.125d, e.evaluate("(/ 1 8)"));
	}
	@Test
	public void testExpression_modulo_01() {
		assertEquals(2d, e.evaluate("(% 8 3)"));
	}
	@Test
	public void testExpression_equals_01() {
		assertEquals(false, e.evaluate("(= 50 5)"));
	}
	@Test
	public void testExpression_equals_02() {
		assertEquals(true, e.evaluate("(= 5.000d 5f 5)"));
	}
	@Test
	public void testExpression_equals_03() {
		assertEquals(false, e.evaluate("(= 5 5 5 1)"));
	}
	@Test
	public void testExpression_lessthan_01() {
		assertEquals(true, e.evaluate("(< 5 6 7)"));
	}
	@Test
	public void testExpression_lessthan_02() {
		assertEquals(false, e.evaluate("(< 100 6)"));
	}
	@Test
	public void testExpression_lessthan_03() {
		assertEquals(false, e.evaluate("(< 6 6)"));
	}
	@Test
	public void testExpression_lessthanorequal_01() {
		assertEquals(true, e.evaluate("(<= 6 6)"));
	}
	@Test
	public void testExpression_greaterthan_01() {
		assertEquals(false, e.evaluate("(> 6 6)"));
	}
	@Test
	public void testExpression_greaterthanorequal_01() {
		assertEquals(true, e.evaluate("(>= 6 6)"));
	}
	@Test
	public void testExpression_and_01() {
		assertEquals(true, e.evaluate("(& (= 1 1) (= 2 2))"));
	}
	@Test
	public void testExpression_or_01() {
		assertEquals(true, e.evaluate("(| (= 1 0) (= 2 2))"));
	}
	@Test
	public void testExpression_and_02() {
		assertEquals(false, e.evaluate("(& (= 1 1) (= 1 2))"));
	}
	@Test
	public void testExpression_or_02() {
		assertEquals(false, e.evaluate("(| (= 1 0) (= 2 1))"));
	}
	@Test
	public void testExpression_if_01() {
		assertEquals(17d, e.evaluate("(+ (if (= 1 0) 18 17) 0)"));
	}
	@Test
	public void testExpression_if_02() {
		assertEquals(18d, e.evaluate("(if (< 1 3) 18 17)"));
	}
	//assertEquals("2011", e.evaluate("(now \"yyyy\")"));// stupid test
	@Test
	public void testExpression_var_01() {
		assertEquals(36, e.evaluate("(set var 3) (set var1 12) (round (* $var $var1))"));
	}
	@Test
	public void testExpression_var_02() {
		assertEquals(144, e.evaluate("(set var 3) (set var 12) (round (* $var $var))"));
	}

	/*
		input + " -> " + ret + " (" +ret.getClass() + ")";
	 */

}
