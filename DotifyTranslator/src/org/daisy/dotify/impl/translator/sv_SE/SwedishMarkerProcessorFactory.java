package org.daisy.dotify.impl.translator.sv_SE;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.daisy.dotify.translator.attributes.DefaultMarkerProcessor;
import org.daisy.dotify.translator.attributes.Marker;
import org.daisy.dotify.translator.attributes.MarkerProcessor;
import org.daisy.dotify.translator.attributes.MarkerProcessorFactory;
import org.daisy.dotify.translator.attributes.RegexMarkerDictionary;
import org.daisy.dotify.translator.attributes.StyleConstants;
import org.daisy.dotify.translator.attributes.TextAttribute;
import org.daisy.dotify.translator.attributes.TextAttributeFilter;

public class SwedishMarkerProcessorFactory implements MarkerProcessorFactory {
	private final static String WHITESPACE_REGEX = "\\s+";
	private final static String ALPHANUM_REGEX = "\\A[a-zA-Z0-9]+\\z";
	private final static FilterLocale sv_SE = FilterLocale.parse("sv-SE");
	

	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return locale.equals(sv_SE) && mode.equals(BrailleTranslatorFactory.MODE_UNCONTRACTED);
	}

	public MarkerProcessor newMarkerProcessor(FilterLocale locale, String mode) throws UnsupportedSpecificationException {
		if (locale.equals(sv_SE) && mode.equals(BrailleTranslatorFactory.MODE_UNCONTRACTED)) {

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

				private boolean checkChildren(TextAttribute atts) {
					if (atts.hasChildren()) {
						for (TextAttribute t : atts) {
							if (t.getDictionaryIdentifier() != null) {
								return false;
							} else {
								if (!checkChildren(t)) {
									return false;
								}
							}
						}
					}
					return true;
				}

				public boolean appliesTo(TextAttribute atts) {
					return checkChildren(atts);
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

			return sap;
		} 
		throw new UnsupportedSpecificationException("Factory does not support " + locale + "/" + mode);
	}

}
