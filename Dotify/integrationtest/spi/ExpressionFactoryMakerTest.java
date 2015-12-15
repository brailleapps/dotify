package spi;

import static org.junit.Assert.assertEquals;

import org.daisy.dotify.api.obfl.Expression;
import org.daisy.dotify.consumer.obfl.ExpressionFactoryMaker;
import org.junit.Ignore;
import org.junit.Test;

public class ExpressionFactoryMakerTest {
	private final Expression e = ExpressionFactoryMaker.newInstance().getFactory().newExpression();
	

	@Test
	public void testExpression_int2text_01() {
		assertEquals("ett", e.evaluate("(int2text 1 sv-se)"));
	}

	@Test
	public void testExpression_int2text_02() {
		assertEquals("två", e.evaluate("(int2text (round 2.3) sv-se)"));
	}

	@Test
	public void testExpression_int2text_03() {
		assertEquals("two", e.evaluate("(int2text (round 2.3) en)"));
	}

	@Test
	public void testExpression_int2text_04() {
		assertEquals("ett", e.evaluate("(int2text 1.0 sv-se)"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testExpression_int2text_05() {
		e.evaluate("(int2text 2.3 sv-se)");
	}

	@Test
	public void testExpression_int2text_06() {
		assertEquals("yksi", e.evaluate("(int2text 1.0 fi)"));
	}
}
