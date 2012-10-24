package org.daisy.dotify.formatter;

import org.daisy.dotify.text.FilterLocale;

/**
 * <p>SpanProperties defines properties specific for a span of text</p>
 * <p>Note that no span properties have been implemented yet</p>
 * 
 * @author Joel Håkansson, TPB
 */
public class TextProperties {
	private final FilterLocale locale;
	private final boolean hyphenate;
	
	/**
	 * Provides a builder for creating text properties instances.
	 * 
	 * @author Joel Håkansson
	 */
	public static class Builder {
		private final FilterLocale locale;
		private boolean hyphenate = true;
		/**
		 * Creates a new builder with the specified locale
		 * @param locale the locale for the builder
		 */
		public Builder(FilterLocale locale) {
			this.locale = locale;
		}
		/**
		 * Sets the hyphenate value for thie builder
		 * @param value the value
		 * @return returns this object
		 */
		public Builder hyphenate(boolean value) {
			this.hyphenate = value;
			return this;
		}
		/**
		 * Builds a new TextProperties object using the current
		 * status of this builder.
		 * @return returns a TextProperties instance
		 */
		public TextProperties build() {
			return new TextProperties(this);
		}
	}
	
	private TextProperties(Builder builder) {
		this.locale = builder.locale;
		this.hyphenate = builder.hyphenate;
	}

	/**
	 * Gets the locale of this text properties
	 * @return returns the locale
	 */
	public FilterLocale getLocale() {
		return locale;
	}

	/**
	 * Returns true if the hyphenating property is true, false otherwise
	 * @return returns true if the hyphenating property is true
	 */
	public boolean isHyphenating() {
		return hyphenate;
	}

}
