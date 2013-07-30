package org.daisy.dotify.impl.translator.sv_SE;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.SimpleBrailleTranslator;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.daisy.dotify.translator.attributes.DefaultMarkerProcessor;
import org.daisy.dotify.translator.attributes.Marker;
import org.daisy.dotify.translator.attributes.RegexMarkerDictionary;
import org.daisy.dotify.translator.attributes.StyleConstants;
import org.daisy.dotify.translator.attributes.TextAttribute;
import org.daisy.dotify.translator.attributes.TextAttributeFilter;

public class SwedishBrailleTranslatorFactory implements BrailleTranslatorFactory {
	private final static String WHITESPACE_REGEX = "\\s+";
	private final static String ALPHANUM_REGEX = "\\A[a-zA-Z0-9]+\\z";
	private final static FilterLocale sv_SE = FilterLocale.parse("sv-SE");
	

	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return locale.equals(sv_SE) && mode.equals(MODE_UNCONTRACTED);
	}

	public BrailleTranslator newTranslator(FilterLocale locale, String mode) throws UnsupportedSpecificationException {
		if (locale.equals(sv_SE) && mode.equals(MODE_UNCONTRACTED)) {

			// Svenska skrivregler för punktskrift 2009, page 34
			RegexMarkerDictionary strong = new RegexMarkerDictionary.Builder().
					addPattern(WHITESPACE_REGEX, new Marker("\u2828\u2828", "\u2831"), new Marker("\u2828", "")).
					build();
			
			// Svenska skrivregler för punktskrift 2009, page 34
			RegexMarkerDictionary em = new RegexMarkerDictionary.Builder().
					addPattern(WHITESPACE_REGEX, new Marker("\u2820\u2824", "\u2831"), new Marker("\u2820\u2804", "")).
					build();

			// Svenska skrivregler för punktskrift 2009, page 32
			TextAttributeFilter subnodeFilter = new TextAttributeFilter() {
				public boolean appliesTo(TextAttribute atts) {
					return !atts.hasChildren();
				}
			};
			RegexMarkerDictionary sub = new RegexMarkerDictionary.Builder().
					addPattern(ALPHANUM_REGEX, new Marker("\u2823", "")).
					filter(subnodeFilter).
					build();

			// Svenska skrivregler för punktskrift 2009, page 32
			RegexMarkerDictionary sup = new RegexMarkerDictionary.Builder().
					addPattern(ALPHANUM_REGEX, new Marker("\u282c", "")).
					filter(subnodeFilter).
					build();

			DefaultMarkerProcessor sap = new DefaultMarkerProcessor.Builder().
					addDictionary(StyleConstants.STRONG, strong).
					addDictionary(StyleConstants.EM, em).
					addDictionary(StyleConstants.SUB, sub).
					addDictionary(StyleConstants.SUP, sup).build();
					

			return new SimpleBrailleTranslator(new SwedishBrailleFilter(), sv_SE, sap);
		} 
		throw new UnsupportedSpecificationException("Factory does not support " + locale + "/" + mode);
	}

}
