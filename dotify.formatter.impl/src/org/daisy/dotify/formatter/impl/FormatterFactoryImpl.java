package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterConfigurationException;
import org.daisy.dotify.api.formatter.FormatterFactory;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;

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
	private ExpressionFactory expressionFactory;

	public Formatter newFormatter(String locale, String mode) {
		BrailleTranslator t;
		try {
			t = translatorFactory.newFactory(locale, mode).newTranslator(locale, mode);
		} catch (TranslatorConfigurationException e) {
			t = null;
		}
		return new FormatterImpl(t, expressionFactory);
	}

	@Reference
	public void setTranslator(BrailleTranslatorFactoryMakerService translatorFactory) {
		this.translatorFactory = translatorFactory;
	}

	public void unsetTranslator() {
		this.translatorFactory = null;
	}
	
	@Reference
	public void setExpressionFactory(ExpressionFactory expFactory) {
		this.expressionFactory = expFactory;
	}
	
	public void unsetExpressionFactory() {
		this.expressionFactory = null;
	}

	public <T> void setReference(Class<T> c, T reference)
			throws FormatterConfigurationException {
		if (c.equals(BrailleTranslatorFactoryMakerService.class)) {
			setTranslator((BrailleTranslatorFactoryMakerService)reference);
		} else if (c.equals(ExpressionFactory.class))  {
			setExpressionFactory((ExpressionFactory)reference);
		} else {
			throw new FormatterConfigurationException("Unrecognized reference: " + reference);
		}
		
	}

}
