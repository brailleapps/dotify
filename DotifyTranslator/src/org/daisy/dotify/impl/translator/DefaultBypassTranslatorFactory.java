package org.daisy.dotify.impl.translator;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.IdentityFilter;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.SimpleBrailleTranslator;
import org.daisy.dotify.translator.UnsupportedSpecificationException;

public class DefaultBypassTranslatorFactory implements BrailleTranslatorFactory {

	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return mode.equals(MODE_BYPASS);
	}

	public BrailleTranslator newTranslator(FilterLocale locale, String mode) throws UnsupportedSpecificationException {
		if (mode.equals(MODE_BYPASS)) {
			return new SimpleBrailleTranslator(new IdentityFilter(), locale);
		}
		throw new UnsupportedSpecificationException("Factory does not support " + locale + "/" + mode);
	}

}
