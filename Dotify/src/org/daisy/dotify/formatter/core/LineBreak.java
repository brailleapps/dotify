package org.daisy.dotify.formatter.core;

import org.daisy.dotify.formatter.dom.EventContents;


public class LineBreak implements EventContents {

	public ContentType getContentType() {
		return ContentType.BR;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
