package org.daisy.dotify.formatter.dom;

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
	
	public static class Builder {
		private final FilterLocale locale;
		private boolean hyphenate = true;
		public Builder(FilterLocale locale) {
			this.locale = locale;
		}
		public Builder hyphenate(boolean value) {
			this.hyphenate = value;
			return this;
		}
		public TextProperties build() {
			return new TextProperties(this);
		}
	}
	
	private TextProperties(Builder builder) {
		this.locale = builder.locale;
		this.hyphenate = builder.hyphenate;
	}

	public FilterLocale getLocale() {
		return locale;
	}

	public boolean isHyphenating() {
		return hyphenate;
	}

}
