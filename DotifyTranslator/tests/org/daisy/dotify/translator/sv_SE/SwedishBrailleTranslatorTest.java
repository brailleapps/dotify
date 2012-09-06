package org.daisy.dotify.translator.sv_SE;
import static org.junit.Assert.assertEquals;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.BrailleTranslatorResult;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.junit.Test;

public class SwedishBrailleTranslatorTest {
	private final static String TEST_INPUT_STRING_1 = "Skillnaden mellan arbets- och vilodagar blev mindre skarp; hon kunde tillåta sig vilodagar mitt i veckan.";
	private final BrailleTranslator translator;
	private final BrailleTranslator bypass;
	
	public SwedishBrailleTranslatorTest() throws UnsupportedSpecificationException {
		FilterLocale sv_SE = FilterLocale.parse("sv-SE");
		this.translator = new SwedishBrailleTranslatorFactory().newTranslator(sv_SE, BrailleTranslatorFactory.MODE_UNCONTRACTED);
		this.translator.setHyphenating(true);
		this.bypass = new SwedishBrailleTranslatorFactory().newTranslator(sv_SE, BrailleTranslatorFactory.MODE_BYPASS);
	}
	
	@Test
	public void testBypass() {
		//Setup
		String text = "This is a test to see if the bypass feature works as intended";
		BrailleTranslatorResult btr = bypass.translate(text);
		//Test
		assertEquals("Assert that the output is equal to the input.", text, btr.nextTranslatedRow(100, false));
	}
	
	@Test
	public void testBypassZeroWidthSpace_01() {	
		BrailleTranslatorResult btr = bypass.translate("CD-versionen");
		assertEquals("CD-versionen", btr.getTranslatedRemainder());
	}
	
	@Test
	public void testBypassZeroWidthSpace_02() {	
		BrailleTranslatorResult btr = bypass.translate("CD-versionen");
		assertEquals("CD-", btr.nextTranslatedRow(4, false));
	}
	
	@Test
	public void testBypassZeroWidthSpace_03() {
		//Setup
		BrailleTranslatorResult btr = bypass.translate("CD-versionen");
		btr.nextTranslatedRow(3, false);
		
		//Test
		assertEquals("versionen", btr.getTranslatedRemainder());
	}

	@Test
	public void testTranslator() {
		//Setup
		BrailleTranslatorResult btr = translator.translate(TEST_INPUT_STRING_1);
		//Test
		assertEquals("Assert that the output is translated.", 
				"⠠⠎⠅⠊⠇⠇⠝⠁⠙⠑⠝\u2800⠍⠑⠇⠇⠁⠝\u2800⠁⠗⠃⠑⠞⠎\u2824\u2800⠕⠉⠓\u2800⠧⠊⠇⠕⠙⠁⠛⠁⠗\u2800⠃⠇⠑⠧\u2800⠍⠊⠝⠙⠗⠑\u2800⠎⠅⠁⠗⠏⠆\u2800⠓⠕⠝\u2800⠅⠥⠝⠙⠑\u2800⠞⠊⠇⠇⠡⠞⠁\u2800⠎⠊⠛\u2800⠧⠊⠇⠕⠙⠁⠛⠁⠗\u2800⠍⠊⠞⠞\u2800⠊\u2800⠧⠑⠉⠅⠁⠝⠄",
				btr.getTranslatedRemainder());
	}
	
	@Test
	public void testTranslator_01firstRow() {
		//Setup
		BrailleTranslatorResult btr = translator.translate(TEST_INPUT_STRING_1);

		//Test
		assertEquals("Assert that limit is handled correctly.", 
				"⠠⠎⠅⠊⠇⠇⠝⠁⠙⠑⠝\u2800⠍⠑⠇⠇⠁⠝\u2800⠁⠗⠃⠑⠞⠎\u2824",
				btr.nextTranslatedRow(26, false));
	}

	@Test
	public void testTranslatorWithAnotherLanguage() throws UnsupportedLocaleException {
		//Setup
		FilterLocale en = FilterLocale.parse("en");
		BrailleTranslatorResult btr = translator.translate(TEST_INPUT_STRING_1, en);
		//Test
		assertEquals("Assert that the output is translated.", 
				"⠠⠎⠅⠊⠇⠇⠝⠁⠙⠑⠝\u2800⠍⠑⠇⠇⠁⠝\u2800⠁⠗⠃⠑⠞⠎\u2824\u2800⠕⠉⠓\u2800⠧⠊⠇⠕⠙⠁⠛⠁⠗\u2800⠃⠇⠑⠧\u2800⠍⠊⠝⠙⠗⠑\u2800⠎⠅⠁⠗⠏⠆\u2800⠓⠕⠝\u2800⠅⠥⠝⠙⠑\u2800⠞⠊⠇⠇⠡⠞⠁\u2800⠎⠊⠛\u2800⠧⠊⠇⠕⠙⠁⠛⠁⠗\u2800⠍⠊⠞⠞\u2800⠊\u2800⠧⠑⠉⠅⠁⠝⠄",
				btr.getTranslatedRemainder());
	}

}
