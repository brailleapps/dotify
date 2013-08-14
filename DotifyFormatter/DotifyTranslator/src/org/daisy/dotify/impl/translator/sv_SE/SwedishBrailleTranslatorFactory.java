package org.daisy.dotify.impl.translator.sv_SE;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.SimpleBrailleTranslator;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.daisy.dotify.translator.attributes.MarkerProcessor;

public class SwedishBrailleTranslatorFactory implements BrailleTranslatorFactory {
	private final static FilterLocale sv_SE = FilterLocale.parse("sv-SE");
	
	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return locale.equals(sv_SE) && mode.equals(MODE_UNCONTRACTED);
	}

	public BrailleTranslator newTranslator(FilterLocale locale, String mode) throws UnsupportedSpecificationException {
		if (locale.equals(sv_SE) && mode.equals(MODE_UNCONTRACTED)) {

			MarkerProcessor sap = new SwedishMarkerProcessorFactory().newMarkerProcessor(locale, mode);

			return new SimpleBrailleTranslator(new SwedishBrailleFilter(), sv_SE, mode, sap);
		} 
		throw new UnsupportedSpecificationException("Factory does not support " + locale + "/" + mode);
	}

}
