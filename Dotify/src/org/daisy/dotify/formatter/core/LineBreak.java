package org.daisy.dotify.formatter.core;

import org.daisy.dotify.formatter.dom.EventContents;

/**
 * Provides a line break event object.
 * @author Joel Håkansson
 *
 */
public class LineBreak implements EventContents {

	public ContentType getContentType() {
		return ContentType.BR;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
