package org.daisy.dotify.writer.impl;

import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.api.writer.PagedMediaWriterConfigurationException;
import org.daisy.dotify.api.writer.PagedMediaWriterFactory;
import org.daisy.dotify.writer.TextMediaWriter;

class TextMediaWriterFactory implements PagedMediaWriterFactory {
	private final static String FEATURE_ENCODING_KEY = "encoding";
	private String encoding = "utf-8";

	public PagedMediaWriter newPagedMediaWriter(String mediaType)
			throws PagedMediaWriterConfigurationException {
		return new TextMediaWriter(encoding);
	}

	public Object getFeature(String key) {
		if (FEATURE_ENCODING_KEY.equals(key)) {
			return encoding;
		} else {
			return null;
		}
	}

	public void setFeature(String key, Object value)
			throws PagedMediaWriterConfigurationException {
		if (FEATURE_ENCODING_KEY.equals(key)) {
			encoding = value.toString();
		} else {
			throw new TextMediaWriterConfigurationException("Unknown feature: " + key);
		}
	}
	
	private class TextMediaWriterConfigurationException extends PagedMediaWriterConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = -2673985749596696888L;

		public TextMediaWriterConfigurationException(String message) {
			super(message);
		}
		
	}

}
