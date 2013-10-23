package org.daisy.dotify.impl.hyphenator.latex;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactory;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.daisy.dotify.text.FilterLocale;

public class LatexHyphenatorFactory implements HyphenatorFactory {
	
	/**
	 * Constructs a new LatexHypenator to be used by a hyphenator factory.
	 */
	public LatexHyphenatorFactory() {
	}

	public HyphenatorInterface newHyphenator(String locale) throws HyphenatorConfigurationException {
		return new LatexHyphenator(FilterLocale.parse(locale));
	}

	public Object getFeature(String key) {
		return null;
	}

	public void setFeature(String key, Object value) throws HyphenatorConfigurationException {
		throw new LatexHyphenatorConfigurationException();
	}

}