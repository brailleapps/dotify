package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a formatter proxy implementation. This class is intended to be instantiated
 * by the formatter factory, and is not part of the public API.
 * @author Joel Håkansson
 */
public class FormatterFactoryImpl implements FormatterFactory {

	public Formatter newFormatter(FilterLocale locale, String mode) {
		return new FormatterImpl(locale, mode);
	}

}
