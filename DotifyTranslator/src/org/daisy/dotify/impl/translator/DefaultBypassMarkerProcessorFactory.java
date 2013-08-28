package org.daisy.dotify.impl.translator;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.UnsupportedSpecificationException;
import org.daisy.dotify.translator.attributes.DefaultMarkerProcessor;
import org.daisy.dotify.translator.attributes.Marker;
import org.daisy.dotify.translator.attributes.MarkerProcessor;
import org.daisy.dotify.translator.attributes.MarkerProcessorFactory;
import org.daisy.dotify.translator.attributes.SimpleMarkerDictionary;
import org.daisy.dotify.translator.attributes.StyleConstants;

public class DefaultBypassMarkerProcessorFactory implements
		MarkerProcessorFactory {

	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return mode.equals(BrailleTranslatorFactory.MODE_BYPASS);
	}

	public MarkerProcessor newMarkerProcessor(FilterLocale locale, String mode) throws UnsupportedSpecificationException {
		if (mode.equals(BrailleTranslatorFactory.MODE_BYPASS)) {
			SimpleMarkerDictionary dd = new SimpleMarkerDictionary(new Marker("* ", ""));

			DefaultMarkerProcessor sap = new DefaultMarkerProcessor.Builder().addDictionary(StyleConstants.DD, dd).build();
			return sap;
		}
		throw new UnsupportedSpecificationException("Factory does not support " + locale + "/" + mode);
	}

}
