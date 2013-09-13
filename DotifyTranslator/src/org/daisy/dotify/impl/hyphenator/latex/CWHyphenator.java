package org.daisy.dotify.impl.hyphenator.latex;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.text.FilterLocale;

class CWHyphenator extends AbstractHyphenator {
	private final CWHyphenatorAtom hyphenator;
	private final int accuracy;

	public CWHyphenator(FilterLocale locale, int accuracy) throws HyphenatorConfigurationException {
		this.hyphenator = CWHyphenatorCore.getInstance().getHyphenator(locale);
		this.beginLimit = hyphenator.getDefaultBeginLimit();
		this.endLimit = hyphenator.getDefaultEndLimit();
		this.accuracy = accuracy;
	}

	static boolean supportsLocale(FilterLocale locale) {
		return CWHyphenatorCore.getInstance().supportsLocale(locale);
	}

	public String hyphenate(String phrase) {
		return hyphenator.hyphenate(phrase, getBeginLimit(), getEndLimit(), accuracy);
	}

}
