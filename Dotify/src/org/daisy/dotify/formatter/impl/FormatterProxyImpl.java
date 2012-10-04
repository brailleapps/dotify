package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterProxy;
import org.daisy.dotify.formatter.impl.formatter.FormatterImpl;

/**
 * Provides a formatter proxy implementation. This class is intended to be instantiated
 * by the formatter factory, and is not part of the public API.
 * @author Joel Håkansson
 */
public class FormatterProxyImpl implements FormatterProxy {

	public Formatter newFormatter() {
		return new FormatterImpl();
	}

}
