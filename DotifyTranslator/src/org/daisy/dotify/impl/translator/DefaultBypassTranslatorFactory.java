package org.daisy.dotify.impl.translator;

import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.text.IdentityFilter;
import org.daisy.dotify.translator.SimpleBrailleTranslator;

public class DefaultBypassTranslatorFactory implements BrailleTranslatorFactory {

	public boolean supportsSpecification(String locale, String mode) {
		return mode.equals(MODE_BYPASS);
	}

	public BrailleTranslator newTranslator(String locale, String mode) throws TranslatorConfigurationException {
		if (mode.equals(MODE_BYPASS)) {
			return new SimpleBrailleTranslator(new IdentityFilter(), locale, mode);
		}
		throw new DefaultBypassTranslatorConfigurationException("Factory does not support " + locale + "/" + mode);
	}
	
	private class DefaultBypassTranslatorConfigurationException extends TranslatorConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7094151522287723445L;

		private DefaultBypassTranslatorConfigurationException(String message) {
			super(message);
		}
	}

}
