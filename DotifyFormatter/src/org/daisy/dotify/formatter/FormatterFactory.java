package org.daisy.dotify.formatter;

import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a proxy for creating a formatter implementation. Objects of this class
 * are detected by the formatter factory and their sole purpose is to create
 * instances of a formatter implementation.
 * @author Joel Håkansson
 */
public interface FormatterFactory {
	
	/**
	 * Creates a new formatter.
	 * @return returns the new formatter.
	 */
	public Formatter newFormatter(FilterLocale locale, String mode);

}