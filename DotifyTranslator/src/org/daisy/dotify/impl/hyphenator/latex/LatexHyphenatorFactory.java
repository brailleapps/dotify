package org.daisy.dotify.impl.hyphenator.latex;

import org.daisy.dotify.hyphenator.FeatureNotSupportedException;
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

	public Object getFeature(String key) {
		return null;
	}

	public void setFeature(String key, Object value) throws FeatureNotSupportedException {
		throw new FeatureNotSupportedException();
	}

}