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
	@Test
	public void testCompoundWord_Sv_027() throws UnsupportedLocaleException {
		assertEquals("olycks­falls­för­säk­ring­ar­nas", hyph_sv_SE.hyphenate("olycksfallsförsäkringarnas"));
	}
	@Test
	public void testWord_Sv_028() throws UnsupportedLocaleException {
		assertEquals("ox­hu­vud", hyph_sv_SE.hyphenate("oxhuvud"));
	}
	@Test
	public void testCompoundWord_Sv_029() throws UnsupportedLocaleException {
		assertEquals("chat­pro­gram­met", hyph_sv_SE.hyphenate("chatprogrammet"));
	}
	@Test
	public void testWord_Sv_030() throws UnsupportedLocaleException {
		assertEquals("pap­pers­ark", hyph_sv_SE.hyphenate("pappersark"));
	}
	@Test
	@Ignore
	public void testWord_Sv_031() throws UnsupportedLocaleException {
		assertEquals("skol­orkes­terns", hyph_sv_SE.hyphenate("skolorkesterns"));
	}
	@Test
	public void testWord_Sv_032() throws UnsupportedLocaleException {
		assertEquals("nit­ton­åring­ens", hyph_sv_SE.hyphenate("nittonåringens"));
	}
	@Test
	@Ignore
	public void testCompoundWord_Sv_033() throws UnsupportedLocaleException {
		assertEquals("blom­odlingspurt­seger", hyph_sv_SE.hyphenate("blomodlingspurtseger"));
	}
	@Test
	public void testWord_Sv_034() throws UnsupportedLocaleException {
		assertEquals("Un­der­sö­ka", hyph_sv_SE.hyphenate("Undersöka"));
	}
	@Test
	public void testWord_Sv_035() throws UnsupportedLocaleException {
		assertEquals("kam­rat­skap", hyph_sv_SE.hyphenate("kamratskap"));
	}
	@Test
	public void testWord_Sv_036() throws UnsupportedLocaleException {
		assertEquals("trap­pan", hyph_sv_SE.hyphenate("trappan"));
	}
	@Test
	@Ignore
	public void testCompoundWord_Sv_037() throws UnsupportedLocaleException {
		assertEquals("sprit­auran", hyph_sv_SE.hyphenate("spritauran"));
	}
	@Test
	@Ignore
	public void testCompoundWord_Sv_038() throws UnsupportedLocaleException {
		assertEquals("Stu­dent­åren", hyph_sv_SE.hyphenate("Studentåren"));
	}
	@Test
	@Ignore
	public void testCompoundWord_Sv_039() throws UnsupportedLocaleException {
		assertEquals("baryton­smattrandet", hyph_sv_SE.hyphenate("barytonsmattrandet"));
	}
	@Test
	public void testCompoundWord_Sv_040() throws UnsupportedLocaleException {
		assertEquals("maj­stång", hyph_sv_SE.hyphenate("majstång"));
	}
	@Test
	public void testWord_Sv_041() throws UnsupportedLocaleException {
		assertEquals("av­sli­ten", hyph_sv_SE.hyphenate("avsliten"));
	}
	@Test
	public void testWord_Sv_042() throws UnsupportedLocaleException {
		assertEquals("ho­nom", hyph_sv_SE.hyphenate("honom"));
	}
	@Test
	public void testCompoundWord_Sv_043() throws UnsupportedLocaleException {
		assertEquals("Upp­hovs­rätts­la­gen", hyph_sv_SE.hyphenate("Upphovsrättslagen"));
	}
	@Test
	@Ignore
	public void testWord_Sv_044() throws UnsupportedLocaleException {
		assertEquals("Transport", hyph_sv_SE.hyphenate("Transport"));
	}
	@Test
	public void testWord_Sv_045() throws UnsupportedLocaleException {
		assertEquals("eds­brotts", hyph_sv_SE.hyphenate("edsbrotts"));
	}
	@Test
	public void testWord_Sv_046() throws UnsupportedLocaleException {
		assertEquals("för­fal­lo­tid", hyph_sv_SE.hyphenate("förfallotid"));
	}
	@Test
	public void testWord_Sv_047() throws UnsupportedLocaleException {
		assertEquals("dår­skap", hyph_sv_SE.hyphenate("dårskap"));
	}
	@Test
	public void testCompoundWord_Sv_048() throws UnsupportedLocaleException {
		assertEquals("upp­hovs­rätts­la­gar", hyph_sv_SE.hyphenate("upphovsrättslagar"));
	}
	@Test
	public void testWord_Sv_049() throws UnsupportedLocaleException {
		assertEquals("runt­om", hyph_sv_SE.hyphenate("runtom"));
	}
	@Test
	public void testCompoundWord_Sv_050() throws UnsupportedLocaleException {
		assertEquals("ori­gi­nal­för­lag", hyph_sv_SE.hyphenate("originalförlag"));
	}
	@Test
	public void testWord_Sv_051() throws UnsupportedLocaleException {
		assertEquals("för­sla­va", hyph_sv_SE.hyphenate("förslava"));
	}
	@Test
	public void testCompoundWord_Sv_052() throws UnsupportedLocaleException {
		assertEquals("sam­hälls­ste­gen", hyph_sv_SE.hyphenate("samhällsstegen"));
	}
	@Test
	@Ignore
	public void testCompoundWord_Sv_053() throws UnsupportedLocaleException {
		assertEquals("kan­vas­tält", hyph_sv_SE.hyphenate("kanvastält"));
	}
	@Test
	public void testWord_Sv_054() throws UnsupportedLocaleException {
		assertEquals("för­strött", hyph_sv_SE.hyphenate("förstrött"));
	}
	@Test
	public void testCompoundWord_Sv_055() throws UnsupportedLocaleException {
		//compound possibilities: as-kungen, ask-ungen
		assertEquals("ask­ung­en", hyph_sv_SE.hyphenate("askungen"));
	}
	@Test
	public void testWord_Sv_056() throws UnsupportedLocaleException {
		assertEquals("önska­des", hyph_sv_SE.hyphenate("önskades"));
	}
	@Test
	public void testWord_Sv_057() throws UnsupportedLocaleException {
		assertEquals("han­nahs", hyph_sv_SE.hyphenate("hannahs"));
	}
	@Test
	public void testCompoundWord_058() {
		assertEquals("alp­land­skap", hyph_sv_SE.hyphenate("alplandskap"));
	}
	@Test
	public void testWord_059() {
		assertEquals("land­skap", hyph_sv_SE.hyphenate("landskap"));
	}
	@Test
	@Ignore
	public void testCompoundWord_060() {
		assertEquals("in­di­ka­torslam­pa", hyph_sv_SE.hyphenate("indikatorslampa"));
	}
	@Test
	public void testCompoundWord_061() {
		//compound possibilities: bil-drulle, bild-rulle
		assertEquals("bildrul­le", hyph_sv_SE.hyphenate("bildrulle"));
	}
	@Test
	public void testCompoundWord_062() {
		assertEquals("skriv­bords­lam­pa", hyph_sv_SE.hyphenate("skrivbordslampa"));
	}
	@Test
	@Ignore
	public void testCompoundWord_063() {
		assertEquals("kon­tors­lam­pa", hyph_sv_SE.hyphenate("kontorslampa"));
	}

}