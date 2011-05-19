package org.daisy.dotify.system.tasks.layout.text.brailleFilters;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;

import org.daisy.dotify.system.tasks.layout.text.FilterLocale;
import org.daisy.dotify.system.tasks.layout.text.StringFilter;
import org.daisy.dotify.translator.BrailleFilterFactory;
import org.junit.Test;

//TODO: move implementation and tests to separate class files
public class BrailleFilterFactoryTest {

	@Test
	public void testSwedishFilter_1_2() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);
		
		// 1.2 - Numbers
		assertEquals("⠼⠃⠚⠚⠊", filter.filter("2009"));
	}

	@Test
	public void testSwedishFilter_2_1() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);
		
		// 2.1 - Punctuation
		assertEquals("⠠⠓⠕⠝ ⠅⠪⠏⠞⠑ ⠎⠍⠪⠗⠂ ⠞⠑ ⠕⠉⠓ ⠕⠎⠞⠄", filter.filter("Hon köpte smör, te och ost."));
		assertEquals("⠠⠅⠕⠍⠍⠑⠗ ⠙⠥⠢", filter.filter("Kommer du?"));
		assertEquals("⠠⠓⠪⠗ ⠥⠏⠏⠖", filter.filter("Hör upp!"));
		assertEquals("⠠⠓⠕⠝ ⠎⠁⠒ ⠠⠠⠙⠝⠒⠎ ⠗⠑⠙⠁⠅⠞⠊⠕⠝ ⠜⠗ ⠎⠞⠕⠗⠄", filter.filter("Hon sa: DN:s redaktion är stor."));
		assertEquals("⠠⠎⠅⠊⠇⠇⠝⠁⠙⠑⠝ ⠍⠑⠇⠇⠁⠝ ⠁⠗⠃⠑⠞⠎- ⠕⠉⠓ ⠧⠊⠇⠕⠙⠁⠛⠁⠗ ⠃⠇⠑⠧ ⠍⠊⠝⠙⠗⠑ ⠎⠅⠁⠗⠏⠆ ⠓⠕⠝ ⠅⠥⠝⠙⠑ ⠞⠊⠇⠇⠡⠞⠁ ⠎⠊⠛ ⠧⠊⠇⠕⠙⠁⠛⠁⠗ ⠍⠊⠞⠞ ⠊ ⠧⠑⠉⠅⠁⠝⠄", filter.filter("Skillnaden mellan arbets- och vilodagar blev mindre skarp; hon kunde tillåta sig vilodagar mitt i veckan."));
		assertEquals("⠠⠍⠌⠠⠎ ⠠⠅⠗⠕⠝⠁⠝", filter.filter("M/S Kronan"));
		assertEquals("⠼⠚⠂⠑⠑ ⠇⠊⠞⠑⠗⠌⠍⠊⠇", filter.filter("0,55 liter/mil"));
		assertEquals("⠍⠡⠝⠁⠙⠎⠎⠅⠊⠋⠞⠑⠞ ⠁⠏⠗⠊⠇⠌⠍⠁⠚", filter.filter("månadsskiftet april/maj"));
		assertEquals("⠰⠠⠧⠊⠇⠇ ⠙⠥ ⠇⠑⠅⠁⠢⠰", filter.filter("\"Vill du leka?\""));
		assertEquals("⠠⠙⠑⠞ ⠧⠁⠗ ⠠⠊⠗⠊⠎⠐ ⠃⠇⠕⠍⠍⠕⠗⠄", filter.filter("Det var Iris' blommor."));
		assertEquals("⠰⠠⠧⠁⠙ ⠃⠑⠞⠽⠙⠑⠗ ⠐⠁⠃⠎⠞⠗⠥⠎⠐⠢⠰ ⠋⠗⠡⠛⠁⠙⠑ ⠓⠁⠝⠄", filter.filter("\"Vad betyder 'abstrus'?\" frågade han."));
	}
	
	@Test
	public void testSwedishFilter_2_2() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);

		// 2.2 - Dashes
		assertEquals("⠠⠁⠝⠝⠑-⠠⠍⠁⠗⠊⠑ ⠓⠁⠗ ⠛⠥⠇- ⠕⠉⠓ ⠧⠊⠞⠗⠁⠝⠙⠊⠛ ⠅⠚⠕⠇⠄", filter.filter("Anne-Marie har gul- och vitrandig kjol."));
		assertEquals("⠠⠑⠞⠞ ⠋⠑⠃⠗⠊⠇⠞ ⠎⠽⠎⠎⠇⠁⠝⠙⠑ ⠍⠑⠙ ⠤⠤ ⠊⠝⠛⠑⠝⠞⠊⠝⠛ ⠁⠇⠇⠎⠄", filter.filter("Ett febrilt sysslande med \u2013 ingenting alls."));
		assertEquals("⠤⠤ ⠠⠧⠁⠙ ⠓⠑⠞⠑⠗ ⠓⠥⠝⠙⠑⠝⠢", filter.filter("\u2013 Vad heter hunden?"));
		assertEquals("⠠⠓⠁⠝ ⠞⠕⠛ ⠞⠡⠛⠑⠞ ⠠⠎⠞⠕⠉⠅⠓⠕⠇⠍⠤⠤⠠⠛⠪⠞⠑⠃⠕⠗⠛⠄", filter.filter("Han tog tåget Stockholm\u2013Göteborg."));
	}
	
	@Test
	public void testSwedishFilter_2_3() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);

		// 2.3.1 - Parentheses
		assertEquals("⠠⠎⠽⠝⠎⠅⠁⠙⠁⠙⠑⠎ ⠠⠗⠊⠅⠎⠋⠪⠗⠃⠥⠝⠙ ⠦⠠⠠⠎⠗⠋⠴", filter.filter("Synskadades Riksförbund (SRF)"));
		assertEquals("⠠⠗⠁⠏⠏⠕⠗⠞⠑⠗ ⠁⠴ ⠋⠗⠡⠝ ⠋⠪⠗⠃⠥⠝⠙⠎⠍⠪⠞⠑⠞ ⠃⠴ ⠅⠁⠎⠎⠁⠜⠗⠑⠝⠙⠑⠝", filter.filter("Rapporter a) från förbundsmötet b) kassaärenden"));
		// 2.3.2 - Brackets
		assertEquals("⠠⠅⠗⠁⠧⠑⠞ ⠓⠁⠗ ⠎⠞⠜⠇⠇⠞⠎ ⠋⠗⠡⠝ ⠕⠇⠊⠅⠁ ⠛⠗⠥⠏⠏⠑⠗ ⠦⠃⠇⠄⠁⠄ ⠷⠓⠪⠛⠎⠅⠕⠇⠑⠾⠎⠞⠥⠙⠑⠗⠁⠝⠙⠑ ⠕⠉⠓ ⠙⠑⠇⠞⠊⠙⠎⠁⠗⠃⠑⠞⠁⠝⠙⠑⠴ ⠍⠑⠝ ⠙⠑⠞ ⠓⠁⠗ ⠁⠇⠇⠞⠊⠙ ⠁⠧⠧⠊⠎⠁⠞⠎⠄", filter.filter("Kravet har ställts från olika grupper (bl.a. [högskole]studerande och deltidsarbetande) men det har alltid avvisats."));
		assertEquals("⠠⠗⠑⠙ ⠠⠏⠕⠗⠞ ⠷⠗⠜⠙ ⠏⠡⠗⠞⠾", filter.filter("Red Port [räd pårt]"));
		// COULDDO 2.3.4
		// 2.3.5 - Braces
		assertEquals("⠠⠷⠼⠁⠂ ⠼⠉⠂ ⠼⠑⠠⠾ ⠥⠞⠇⠜⠎⠑⠎ ⠍⠜⠝⠛⠙⠑⠝ ⠁⠧ ⠞⠁⠇⠑⠝ ⠑⠞⠞⠂ ⠞⠗⠑ ⠕⠉⠓ ⠋⠑⠍⠄", filter.filter("{1, 3, 5} utläses mängden av talen ett, tre och fem."));
	}
	
	@Test
	public void testSwedishFilter_2_4() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);

		// 2.4.1 (ex 2) COULDDO ex 1, 3
		assertEquals("⠎⠑ ⠬ ⠼⠛⠤⠤⠼⠊", filter.filter("se § 7\u20139"));
		// 2.4.2
		assertEquals("⠠⠁⠇⠍⠟⠧⠊⠎⠞ ⠯ ⠠⠺⠊⠅⠎⠑⠇⠇", filter.filter("Almqvist & Wiksell"));
		// 2.4.3 COULDDO ex 2, 3
		assertEquals("⠠⠇⠁⠗⠎ ⠠⠛⠥⠎⠞⠁⠋⠎⠎⠕⠝ ⠔⠼⠁⠊⠉⠋", filter.filter("Lars Gustafsson *1936"));
		// 2.4.4
		assertEquals("⠞⠗⠽⠉⠅ ⠘⠼⠼⠃⠁⠘⠼", filter.filter("tryck #21#"));
		// 2.4.5, 2.4.7
		assertEquals("⠑⠍⠊⠇⠘⠤⠑⠍⠊⠇⠎⠎⠕⠝⠘⠷⠓⠕⠞⠍⠁⠊⠇⠄⠉⠕⠍", filter.filter("emil_emilsson@hotmail.com"));
		// 2.4.6
		assertEquals("⠠⠉⠒⠘⠌⠠⠠⠺⠊⠝⠙⠕⠺⠎⠘⠌⠎⠽⠎⠞⠑⠍⠘⠌⠇⠕⠛⠊⠝⠺⠼⠉⠁⠄⠙⠇⠇", filter.filter("C:\\WINDOWS\\system\\loginw31.dll"));
		// 2.4.8
		assertEquals("⠁⠇⠅⠸⠁", filter.filter("alk|a"));
		// 2.4.9
		assertEquals("⠎⠥⠃⠎⠞⠄ ⠘⠒⠝ ⠘⠒⠁⠗", filter.filter("subst. ~n ~ar"));

	}

	@Test
	public void testSwedishFilter_2_5() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);

		// 2.5
		assertEquals("⠠⠏⠗⠊⠎⠑⠞ ⠧⠁⠗ ⠼⠑⠚⠚ ⠘⠑⠄", filter.filter("Priset var 500 €."));
	}

	// COULDDO 2.6, 2.7
	
	@Test
	public void testSwedishFilter_3_2() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);
		// 3.2 - Uppercase
		// 3.2.1
		assertEquals("⠠⠓⠁⠝ ⠓⠑⠞⠑⠗ ⠠⠓⠁⠝⠎ ⠕⠉⠓ ⠃⠗⠕⠗ ⠓⠁⠝⠎ ⠓⠑⠞⠑⠗ ⠠⠃⠗⠕⠗⠄", filter.filter("Han heter Hans och bror hans heter Bror."));
		// 3.2.2
		assertEquals("⠠⠠⠎⠁⠧", filter.filter("SAV"));
		assertEquals("⠠⠠⠊⠅⠑⠁⠱⠎ ⠅⠁⠞⠁⠇⠕⠛", filter.filter("IKEAs katalog"));
		assertEquals("⠠⠎⠧⠑⠝⠎⠅⠁ ⠠⠠⠊⠎⠃⠝⠱-⠉⠑⠝⠞⠗⠁⠇⠑⠝", filter.filter("Svenska ISBN-centralen"));
		// 3.2.3
		assertEquals("⠠⠠⠠⠇⠕⠌⠞⠉⠕⠌⠎⠁⠉⠕⠱⠒⠎ ⠠⠃⠗⠽⠎⠎⠑⠇⠅⠕⠝⠞⠕⠗", filter.filter("LO/TCO/SACO:s Brysselkontor"));
		assertEquals("⠠⠠⠠⠎⠽⠝⠎⠅⠁⠙⠁⠙⠑⠎ ⠗⠊⠅⠎⠋⠪⠗⠃⠥⠝⠙⠱", filter.filter("SYNSKADADES RIKSFÖRBUND"));
		assertEquals("⠅⠠⠺⠓⠂ ⠠⠚⠜⠍⠠⠕", filter.filter("kWh, JämO"));
		assertEquals("⠠⠇⠪⠎⠑⠝⠕⠗⠙⠒ ⠕⠠⠧⠃⠠⠑⠠⠛⠚", filter.filter("Lösenord: oVbEGj"));
	}
	
	@Test
	public void testSwedishFilter_3_3() {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);

		// 3.3.1
		assertEquals("⠠⠇⠪⠎⠑⠝⠕⠗⠙⠒ ⠇⠧⠃⠼⠑⠛⠱⠚", filter.filter("Lösenord: lvb57j"));
	}
	
	@Test
	public void testSwedishFilter_additional() throws FileNotFoundException {
		FilterLocale sv_se = FilterLocale.parse("sv-se");
		StringFilter filter = BrailleFilterFactory.newInstance().newStringFilter(sv_se);

		assertEquals("⠘⠦⠎⠞⠚⠜⠗⠝⠁⠘⠴ ⠘⠦⠃⠇⠊⠭⠞⠘⠴ ⠘⠦⠒⠦⠘⠴ ⠘⠦⠒⠴⠘⠴ ⠬⠕", filter.filter("\u066d \u2607 \u2639 \u263a \u00ba"));
		assertEquals("⠠⠝⠑⠛⠁⠞⠊⠧⠁ ⠞⠁⠇⠒ -⠼⠙⠑⠋⠙⠑", filter.filter("Negativa tal: -45645"));
		assertEquals("⠘⠦⠓⠚⠜⠗⠞⠑⠗⠘⠴", filter.filter("\u2665")); // hjärter
	}

}
