package org.daisy.dotify.hyphenator;

import static org.junit.Assert.assertEquals;

import org.daisy.dotify.hyphenator.HyphenatorFactoryMaker;
import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.junit.Ignore;
import org.junit.Test;

public class SwedishHyphenationTest {
	private final HyphenatorInterface hyph_sv_SE;
	public SwedishHyphenationTest() {
		HyphenatorInterface h2;
		try {
			h2 = HyphenatorFactoryMaker.newInstance().newHyphenator(FilterLocale.parse("sv-SE"));
		} catch (UnsupportedLocaleException e) {
			h2 = null;
		}
		hyph_sv_SE = h2;
	}

	@Test
	public void testHyphenation_Sv_ZeroWidthSpace() throws UnsupportedLocaleException {
		assertEquals("CD-\u200bver­sio­nen", hyph_sv_SE.hyphenate("CD-versionen"));
	}
	@Test
	public void testHyphenation_Sv_001() throws UnsupportedLocaleException {
		assertEquals("re­tå", hyph_sv_SE.hyphenate("retå"));
	}
	@Test
	public void testHyphenation_Sv_002() throws UnsupportedLocaleException {
		assertEquals("att", hyph_sv_SE.hyphenate("att"));
	}
	@Test
	public void testCompoundWord_Sv_001() throws UnsupportedLocaleException {
		assertEquals("val­nötska­ka", hyph_sv_SE.hyphenate("valnötskaka"));
	}
	@Test
	public void testWord_Sv_002() throws UnsupportedLocaleException {
		assertEquals("vart­åt", hyph_sv_SE.hyphenate("vartåt"));
	}
	@Test
	public void testCompoundWord_Sv_003() throws UnsupportedLocaleException {
		assertEquals("fisklå­da", hyph_sv_SE.hyphenate("fisklåda"));
	}
	@Test
	public void testCompoundWord_Sv_004() throws UnsupportedLocaleException {
		assertEquals("blå­rött", hyph_sv_SE.hyphenate("blårött"));
	}
	@Test
	public void testCompoundWord_Sv_005() throws UnsupportedLocaleException {
		assertEquals("ro­sen­rött", hyph_sv_SE.hyphenate("rosenrött"));
	}
	@Test
	public void testCompoundWord_Sv_006() throws UnsupportedLocaleException {
		assertEquals("him­mels­blått", hyph_sv_SE.hyphenate("himmelsblått"));
	}
	@Test
	public void testWord_Sv_007() throws UnsupportedLocaleException {
		assertEquals("him­mels", hyph_sv_SE.hyphenate("himmels"));
	}
	@Test
	public void testWord_Sv_008() throws UnsupportedLocaleException {
		assertEquals("blått", hyph_sv_SE.hyphenate("blått"));
	}
	@Test
	public void testWord_Sv_009() throws UnsupportedLocaleException {
		assertEquals("sjut­ton­åring­ar", hyph_sv_SE.hyphenate("sjuttonåringar"));
	}
	@Test
	public void testWord_Sv_010() throws UnsupportedLocaleException {
		assertEquals("ar­ton­åring­ar", hyph_sv_SE.hyphenate("artonåringar"));
	}
	@Test
	public void testWord_Sv_011() throws UnsupportedLocaleException {
		assertEquals("tret­ton­åring", hyph_sv_SE.hyphenate("trettonåring"));
	}
	@Test
	public void testWord_Sv_012() throws UnsupportedLocaleException {
		assertEquals("schysst", hyph_sv_SE.hyphenate("schysst"));
	}
	@Test
	public void testWord_Sv_013() throws UnsupportedLocaleException {
		assertEquals("sel­le­ri", hyph_sv_SE.hyphenate("selleri"));
	}
	@Test
	public void testCompoundWord_Sv_014() throws UnsupportedLocaleException {
		assertEquals("el­stöts­pen­na", hyph_sv_SE.hyphenate("elstötspenna"));
	}
	@Test
	public void testCompoundWord_Sv_015() throws UnsupportedLocaleException {
		assertEquals("test­fil", hyph_sv_SE.hyphenate("testfil"));
	}
	@Test
	public void testWord_Sv_016() throws UnsupportedLocaleException {
		assertEquals("här­om­da­gen", hyph_sv_SE.hyphenate("häromdagen"));
	}
	@Test
	public void testCompoundWord_Sv_017() throws UnsupportedLocaleException {
		assertEquals("skri­var­driv­ru­ti­nen", hyph_sv_SE.hyphenate("skrivardrivrutinen"));
	}
	@Test
	public void testCompoundWord_Sv_018() throws UnsupportedLocaleException {
		assertEquals("ut­skrifts­fil", hyph_sv_SE.hyphenate("utskriftsfil"));
	}
	@Test
	public void testCompoundWord_Sv_019() throws UnsupportedLocaleException {
		assertEquals("tal­boks­lä­sa­re", hyph_sv_SE.hyphenate("talboksläsare"));
	}	
	@Test
	public void testWord_Sv_020() throws UnsupportedLocaleException {
		assertEquals("hämt­mat", hyph_sv_SE.hyphenate("hämtmat"));
	}
	@Test
	public void testCompoundWord_Sv_021() throws UnsupportedLocaleException {
		assertEquals("över­armsöver­an­sträng­ning", hyph_sv_SE.hyphenate("överarmsöveransträngning"));
	}
	@Test
	@Ignore
	public void testCompoundWord_Sv_023() throws UnsupportedLocaleException {
		assertEquals("möns­ter­djups­mä­tar­ap­pa­rat", hyph_sv_SE.hyphenate("mönsterdjupsmätarapparat"));
	}
	@Test
	public void testWord_Sv_024() throws UnsupportedLocaleException {
		assertEquals("hög­sko­le­stu­den­ter", hyph_sv_SE.hyphenate("högskolestudenter"));
	}
	@Test
	public void testWord_Sv_025() throws UnsupportedLocaleException {
		assertEquals("ton­års­flicka", hyph_sv_SE.hyphenate("tonårsflicka"));
	}
	@Test
	public void testWord_Sv_026() throws UnsupportedLocaleException {
		assertEquals("son­son", hyph_sv_SE.hyphenate("sonson"));
	}
}