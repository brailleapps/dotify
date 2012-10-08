package org.daisy.dotify.formatter.obfl;

import org.daisy.dotify.formatter.dom.Marker;

class MarkerEventContents extends Marker implements EventContents {

	public MarkerEventContents(String name, String value) {
		super(name, value);
	}

	public ContentType getContentType() {
		return ContentType.MARKER;
	}
}
