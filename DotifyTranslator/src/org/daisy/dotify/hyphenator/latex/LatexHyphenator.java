package org.daisy.dotify.hyphenator.latex;

import org.daisy.dotify.hyphenator.AbstractHyphenator;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

class LatexHyphenator extends AbstractHyphenator {
	private final net.davidashen.text.Hyphenator hyphenator;
	
	LatexHyphenator(FilterLocale locale) throws UnsupportedLocaleException {
		this.hyphenator = LatexHyphenatorCore.getInstance().getHyphenator(locale);
	}
	
	static boolean supportsLocale(FilterLocale locale) {
		return LatexHyphenatorCore.getInstance().supportsLocale(locale);
	}

	public String hyphenate(String phrase) {
		return hyphenator.hyphenate(phrase, getBeginLimit(), getEndLimit());
	}

}
