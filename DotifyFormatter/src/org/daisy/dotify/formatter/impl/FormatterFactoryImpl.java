package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;
import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;

import aQute.bnd.annotation.component.Component;
import aQute.bnd.annotation.component.Reference;

/**
 * Provides a formatter proxy implementation. This class is intended to be instantiated
 * by the formatter factory, and is not part of the public API.
 * @author Joel Håkansson
 */
@Component
public class FormatterFactoryImpl implements FormatterFactory {
	private BrailleTranslatorFactoryMakerService translatorFactory;

	public Formatter newFormatter(String locale, String mode) {
		BrailleTranslator t;
		try {
			t = translatorFactory.newFactory(locale, mode).newTranslator(locale, mode);
		} catch (TranslatorConfigurationException e) {
			t = null;
		}
		return new FormatterImpl(t);
	}

	@Reference
	public void setTranslator(BrailleTranslatorFactoryMakerService translatorFactory) {
		this.translatorFactory = translatorFactory;
	}

	public void unsetTranslator() {
		this.translatorFactory = null;
	}

}
