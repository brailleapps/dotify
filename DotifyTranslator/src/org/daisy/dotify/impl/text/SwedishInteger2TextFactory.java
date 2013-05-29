package org.daisy.dotify.impl.text;

import org.daisy.dotify.hyphenator.UnsupportedFeatureException;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.Integer2Text;
import org.daisy.dotify.text.Integer2TextFactory;

public class SwedishInteger2TextFactory implements Integer2TextFactory {
	private final static FilterLocale sv_SE = FilterLocale.parse("sv-SE");

	public boolean supportsLocale(FilterLocale locale) {
		return locale.equals(sv_SE);
	}

	public Integer2Text newInteger2Text(FilterLocale locale) throws UnsupportedLocaleException {
		return new SwedishInteger2Text();
	}

	public Object getFeature(String key) {
		return null;
	}

	public void setFeature(String key, Object value) throws UnsupportedFeatureException {
		throw new UnsupportedFeatureException();
	}

}
