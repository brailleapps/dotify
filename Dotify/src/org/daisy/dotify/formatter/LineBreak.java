package org.daisy.dotify.formatter;

public class LineBreak implements EventContents {

	public ContentType getContentType() {
		return ContentType.BR;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
