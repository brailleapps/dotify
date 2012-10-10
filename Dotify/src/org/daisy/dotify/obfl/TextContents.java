package org.daisy.dotify.obfl;

import org.daisy.dotify.formatter.TextProperties;

/**
 * Provides a text event object.
 * @author Joel Håkansson
 *
 */
class TextContents implements EventContents {
	private final String text;
	private final TextProperties p;
	
	public TextContents(String text, TextProperties p) {
		this.text = text;
		this.p = p;
	}

	public ContentType getContentType() {
		return ContentType.PCDATA;
	}

	public String getText() {
		return text;
	}

	public TextProperties getSpanProperties() {
		return p;
	}

	public boolean canContainEventObjects() {
		return false;
	}
}
