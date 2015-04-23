package org.daisy.dotify.impl.translator;

import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactory;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.common.text.IdentityFilter;
import org.daisy.dotify.translator.SimpleBrailleTranslator;

class DefaultBypassTranslatorFactory implements BrailleTranslatorFactory {
	private final HyphenatorFactoryMakerService hyphenatorService;

	DefaultBypassTranslatorFactory(HyphenatorFactoryMakerService hyphenatorService) {
		this.hyphenatorService = hyphenatorService;
	}

	public BrailleTranslator newTranslator(String locale, String mode) throws TranslatorConfigurationException {
		if (hyphenatorService == null) {
			throw new DefaultBypassTranslatorConfigurationException("HyphenatorFactoryMakerService not set.");
		} else if (mode.equals(MODE_BYPASS)) {
			return new SimpleBrailleTranslator(new IdentityFilter(), locale, mode, hyphenatorService);
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
