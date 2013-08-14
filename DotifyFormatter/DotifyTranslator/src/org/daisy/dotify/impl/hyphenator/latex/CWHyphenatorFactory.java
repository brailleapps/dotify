package org.daisy.dotify.impl.hyphenator.latex;

import java.util.logging.Logger;

import org.daisy.dotify.hyphenator.UnsupportedFeatureException;
import org.daisy.dotify.hyphenator.HyphenationFeatures;
import org.daisy.dotify.hyphenator.HyphenatorFactory;
import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

public class CWHyphenatorFactory implements HyphenatorFactory {
	private int accuracy = 5;

	public boolean supportsLocale(FilterLocale locale) {
		return CWHyphenator.supportsLocale(locale);
	}

	public HyphenatorInterface newHyphenator(FilterLocale locale)
			throws UnsupportedLocaleException {
		return new CWHyphenator(locale, accuracy);
	}

	public Object getFeature(String key) {
		if (key.equals(HyphenationFeatures.HYPHENATION_ACCURACY)) {
			return accuracy;
		}
		return null;
	}

	public void setFeature(String key, Object value) throws UnsupportedFeatureException {
		if (key.equals(HyphenationFeatures.HYPHENATION_ACCURACY)) {
			accuracy = (Integer)value;
			if (accuracy !=5 && accuracy != 3) {
				Logger.getLogger(this.getClass().getCanonicalName()).fine(
						"Feature " + HyphenationFeatures.HYPHENATION_ACCURACY + " set to an unsupported value: " + accuracy + ". Supported values are 3 and 5.");
			}
		} else {
			throw new UnsupportedFeatureException();
		}
	}

}