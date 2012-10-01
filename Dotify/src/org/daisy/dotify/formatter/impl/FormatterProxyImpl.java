package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.Formatter;
import org.daisy.dotify.formatter.FormatterProxy;

public class FormatterProxyImpl implements FormatterProxy {

	public Formatter newFormatter() {
		return new FormatterImpl();
	}

}
