package org.daisy.dotify.hyphenator;

import static org.junit.Assert.assertEquals;

import org.daisy.dotify.hyphenator.HyphenatorFactoryMaker;
import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.junit.Ignore;
import org.junit.Test;

public class SwedishHyphenationTest2 {
	private final HyphenatorInterface hyph_sv_SE;
	public SwedishHyphenationTest2() {
		HyphenatorInterface h2;
		try {
			h2 = HyphenatorFactoryMaker.newInstance().newHyphenator(FilterLocale.parse("sv-SE"));
		} catch (UnsupportedLocaleException e) {
			h2 = null;
		}
		hyph_sv_SE = h2;
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