package org.daisy.dotify.impl.translator.sv_SE;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.SimpleBrailleTranslator;
import org.daisy.dotify.translator.TranslatorConfigurationException;
import org.daisy.dotify.translator.attributes.MarkerProcessor;
import org.daisy.dotify.translator.attributes.MarkerProcessorConfigurationException;

public class SwedishBrailleTranslatorFactory implements BrailleTranslatorFactory {
	private final static FilterLocale sv_SE = FilterLocale.parse("sv-SE");
	
	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return locale.equals(sv_SE) && mode.equals(MODE_UNCONTRACTED);
	}

	public BrailleTranslator newTranslator(FilterLocale locale, String mode) throws TranslatorConfigurationException {
		if (locale.equals(sv_SE) && mode.equals(MODE_UNCONTRACTED)) {

			MarkerProcessor sap;
			try {
				sap = new SwedishMarkerProcessorFactory().newMarkerProcessor(locale, mode);
			} catch (MarkerProcessorConfigurationException e) {
				throw new SwedishTranslatorConfigurationException(e);
			}

			return new SimpleBrailleTranslator(new SwedishBrailleFilter(), sv_SE, mode, sap);
		} 
		throw new SwedishTranslatorConfigurationException("Factory does not support " + locale + "/" + mode);
	}
	
	private class SwedishTranslatorConfigurationException extends TranslatorConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5954729812690753410L;

		public SwedishTranslatorConfigurationException(String message) {
			super(message);
		}

		SwedishTranslatorConfigurationException(Throwable cause) {
			super(cause);
		}
		
	}

}
