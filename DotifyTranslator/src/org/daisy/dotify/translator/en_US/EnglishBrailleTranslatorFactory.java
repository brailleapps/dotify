package org.daisy.dotify.translator.en_US;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.RegexFilter;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.SimpleBrailleTranslator;
import org.daisy.dotify.translator.UnsupportedSpecificationException;

public class EnglishBrailleTranslatorFactory implements BrailleTranslatorFactory {
	private final static FilterLocale en_US = FilterLocale.parse("en-US");

	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return locale.equals(en_US) && (mode.equals(MODE_BYPASS));
	}

	public BrailleTranslator newTranslator(FilterLocale locale, String mode) throws UnsupportedSpecificationException {
		if (locale.equals(en_US) && (mode.equals(MODE_BYPASS))) {
			return new SimpleBrailleTranslator(new RegexFilter("\\u200B", ""), en_US);
		}
		throw new UnsupportedSpecificationException("Factory does not support " + locale + "/" + mode);
	}

}
