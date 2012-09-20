package org.daisy.dotify.hyphenator.latex;

import org.daisy.dotify.hyphenator.AbstractHyphenator;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

class LatexHyphenator extends AbstractHyphenator {
	private final HyphenationConfig hyphenator;
	
	LatexHyphenator(FilterLocale locale) throws UnsupportedLocaleException {
		LatexHyphenatorCore core = LatexHyphenatorCore.getInstance();
		this.hyphenator = core.getHyphenator(locale);
		this.beginLimit = hyphenator.getBeginLimit();
		this.endLimit = hyphenator.getEndLimit();
	}
	
	static boolean supportsLocale(FilterLocale locale) {
		return LatexHyphenatorCore.getInstance().supportsLocale(locale);
	}

	public String hyphenate(String phrase) {
		return hyphenator.getHyphenator().hyphenate(phrase, getBeginLimit(), getEndLimit());
	}

}
