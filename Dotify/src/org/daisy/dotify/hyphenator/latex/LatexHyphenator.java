package org.daisy.dotify.hyphenator.latex;

import org.daisy.dotify.hyphenator.AbstractHyphenator;
import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

public class LatexHyphenator extends AbstractHyphenator {
	private final net.davidashen.text.Hyphenator hyphenator;
	
	/**
	 * Constructs a new LatexHypenator to be used by a hyphenator factory.
	 */
	public LatexHyphenator() {
		this(null);
	}
	
	private LatexHyphenator(net.davidashen.text.Hyphenator hyphenator) {
		this.hyphenator = hyphenator;
	}

	public boolean supportsLocale(FilterLocale locale) {
		return LatexHyphenatorCore.getInstance().supportsLocale(locale);
	}

	public String hyphenate(String phrase) {
		return hyphenator.hyphenate(phrase, beginLimit, endLimit);
	}

	public HyphenatorInterface newInstance(FilterLocale locale) throws UnsupportedLocaleException {
		return new LatexHyphenator(LatexHyphenatorCore.getInstance().getHyphenator(locale));
	}

}