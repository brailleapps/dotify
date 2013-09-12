package org.daisy.dotify.impl.text;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.Integer2Text;
import org.daisy.dotify.text.Integer2TextConfigurationException;
import org.daisy.dotify.text.Integer2TextFactory;

public class EnglishInteger2TextFactory implements Integer2TextFactory {
	private final static FilterLocale en = FilterLocale.parse("en");

	public boolean supportsLocale(FilterLocale locale) {
		return locale.equals(en);
	}

	public Integer2Text newInteger2Text(FilterLocale locale) throws Integer2TextConfigurationException {
		return new BasicInteger2Text(new EnInt2TextLocalization());
	}

	public Object getFeature(String key) {
		return null;
	}

	public void setFeature(String key, Object value) throws Integer2TextConfigurationException {
		throw new EnglishInteger2TextConfigurationException();
	}
	
	private class EnglishInteger2TextConfigurationException extends
			Integer2TextConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7090139699406930899L;
		
	}

}
