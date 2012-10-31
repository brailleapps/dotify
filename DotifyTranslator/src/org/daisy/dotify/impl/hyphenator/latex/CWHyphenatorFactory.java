package org.daisy.dotify.impl.hyphenator.latex;

import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.HyphenatorFactory;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

public class CWHyphenatorFactory implements HyphenatorFactory {

	public boolean supportsLocale(FilterLocale locale) {
		return CWHyphenator.supportsLocale(locale);
	}

	public HyphenatorInterface newHyphenator(FilterLocale locale)
			throws UnsupportedLocaleException {
		return new CWHyphenator(locale);
	}

}