package org.daisy.dotify.obfl;

import org.daisy.dotify.api.formatter.Marker;

class MarkerEventContents extends Marker implements EventContents {

	public MarkerEventContents(String name, String value) {
		super(name, value);
	}

	public ContentType getContentType() {
		return ContentType.MARKER;
	}
}
