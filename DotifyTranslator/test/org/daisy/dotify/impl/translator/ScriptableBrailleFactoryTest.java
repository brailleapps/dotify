package org.daisy.dotify.impl.translator;

import static org.junit.Assert.*;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.junit.Test;

public class ScriptableBrailleFactoryTest {
	private final BrailleTranslator tr;

	public ScriptableBrailleFactoryTest() throws UnsupportedSpecificationException {
		ScriptableBrailleFactory bf = new ScriptableBrailleFactory();
		tr = bf.newTranslator(FilterLocale.parse("sv-SE-script"), "uncontracted");
	}

	@Test
	public void testScriptable() throws UnsupportedSpecificationException {
		
		assertEquals("⠞⠑⠎⠞ ⠼⠁⠃⠱⠁", tr.translate("test 12a").getTranslatedRemainder());
	}

	@Test
	public void testHyphenating() throws UnsupportedSpecificationException {
		tr.setHyphenating(true);
		assertEquals(true, tr.isHyphenating());
		tr.setHyphenating(false);
		assertEquals(false, tr.isHyphenating());
	}

	@Test
	public void testRemaining() throws UnsupportedSpecificationException {
		assertEquals(10, tr.translate("test 12a").countRemaining());
	}

	@Test
	public void testHasNext() throws UnsupportedSpecificationException {
		assertEquals(true, tr.translate("test 12a").hasNext());
	}

}
