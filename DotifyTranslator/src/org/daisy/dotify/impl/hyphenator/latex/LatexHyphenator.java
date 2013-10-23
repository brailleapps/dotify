package org.daisy.dotify.impl.hyphenator.latex;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.text.FilterLocale;

class LatexHyphenator extends AbstractHyphenator {
	private final HyphenationConfig hyphenator;
	
	LatexHyphenator(FilterLocale locale) throws HyphenatorConfigurationException {
		this.hyphenator = LatexHyphenatorCore.getInstance().getHyphenator(locale);
		this.beginLimit = hyphenator.getDefaultBeginLimit();
		this.endLimit = hyphenator.getDefaultEndLimit();
	}
	
	static boolean supportsLocale(String locale) {
		return LatexHyphenatorCore.getInstance().supportsLocale(locale);
	}

	public String hyphenate(String phrase) {
		return hyphenator.getHyphenator().hyphenate(phrase, getBeginLimit(), getEndLimit());
	}

}
