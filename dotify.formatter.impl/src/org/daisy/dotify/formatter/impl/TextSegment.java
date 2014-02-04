package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.TextProperties;



class TextSegment implements Segment, EventContents {
	private String chars;
	private final TextProperties tp;
	
	public TextSegment(String chars, TextProperties tp) {
		this.chars = chars;
		this.tp = tp;
	}
	
	public String getText() {
		return chars;
	}

	public void setText(String chars) {
		this.chars = chars;
	}

	public TextProperties getTextProperties() {
		return tp;
	}

	public SegmentType getSegmentType() {
		return SegmentType.Text;
	}
	
	public ContentType getContentType() {
		return ContentType.PCDATA;
	}
	
	public boolean canContainEventObjects() {
		return false;
	}
	
}
