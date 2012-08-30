package org.daisy.dotify.formatter.core;

import org.daisy.dotify.formatter.dom.EventContents;
import org.daisy.dotify.formatter.dom.TextProperties;


public class TextContents implements EventContents {
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
