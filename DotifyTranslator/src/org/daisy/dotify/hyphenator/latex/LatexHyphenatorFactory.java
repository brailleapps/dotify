package org.daisy.dotify.hyphenator.latex;

import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.HyphenatorFactory;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

public class LatexHyphenatorFactory implements HyphenatorFactory {
	
	/**
	 * Constructs a new LatexHypenator to be used by a hyphenator factory.
	 */
	public LatexHyphenatorFactory() {
	}

	public boolean supportsLocale(FilterLocale locale) {
		return LatexHyphenator.supportsLocale(locale);
	}

	public HyphenatorInterface newHyphenator(FilterLocale locale) throws UnsupportedLocaleException {
		return new LatexHyphenator(locale);
	}

}