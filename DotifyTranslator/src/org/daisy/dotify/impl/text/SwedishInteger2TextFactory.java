package org.daisy.dotify.impl.text;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.Integer2Text;
import org.daisy.dotify.text.Integer2TextFactory;
import org.daisy.dotify.text.Integer2TextConfigurationException;

public class SwedishInteger2TextFactory implements Integer2TextFactory {
	private final static FilterLocale sv_SE = FilterLocale.parse("sv-SE");

	public boolean supportsLocale(FilterLocale locale) {
		return locale.equals(sv_SE);
	}

	public Integer2Text newInteger2Text(FilterLocale locale) throws Integer2TextConfigurationException {
		return new SwedishInteger2Text();
	}

	public Object getFeature(String key) {
		return null;
	}

	public void setFeature(String key, Object value) throws Integer2TextConfigurationException {
		throw new SwedishInteger2TextFactoryFeatureException();
	}
	
	private class SwedishInteger2TextFactoryFeatureException extends
			Integer2TextConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -1918698785748472547L;

		private SwedishInteger2TextFactoryFeatureException() {
			super();
		}
		
	}

}
