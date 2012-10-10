package org.daisy.dotify.obfl;



/**
 * Provides a line break event object.
 * @author Joel Håkansson
 *
 */
class LineBreak implements EventContents {

	public ContentType getContentType() {
		return ContentType.BR;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
