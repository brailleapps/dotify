package org.daisy.dotify.hyphenator.latex;

import org.daisy.dotify.hyphenator.AbstractHyphenator;
import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

public class LatexHyphenator extends AbstractHyphenator {
	private final net.davidashen.text.Hyphenator hyphenator;
	private final FilterLocale locale;
	
	/**
	 * Constructs a new LatexHypenator to be used by a hyphenator factory.
	 */
	public LatexHyphenator() {
		this(null, null);
	}
	
	private LatexHyphenator(net.davidashen.text.Hyphenator hyphenator, FilterLocale locale) {
		this.hyphenator = hyphenator;
		this.locale = locale;
	}

	public boolean supportsLocale(FilterLocale locale) {
		if (this.locale==null) {
			return LatexHyphenatorCore.getInstance().supportsLocale(locale);
		} else {
			return locale.isA(this.locale);
		}
	}

	public String hyphenate(String phrase) {
		return hyphenator.hyphenate(phrase, beginLimit, endLimit);
	}

	public HyphenatorInterface newHyphenator(FilterLocale locale) throws UnsupportedLocaleException {
		return new LatexHyphenator(LatexHyphenatorCore.getInstance().getHyphenator(locale), locale);
	}

}