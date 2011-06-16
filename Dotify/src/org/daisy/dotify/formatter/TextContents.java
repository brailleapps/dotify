package org.daisy.dotify.formatter;

public class TextContents implements BlockContents {
	private final String text;
	
	public TextContents(String text) {
		this.text = text;
	}

	public ContentType getContentType() {
		return ContentType.PCDATA;
	}

	public String getText() {
		return text;
	}
}
