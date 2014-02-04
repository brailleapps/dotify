package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Marker;

class MarkerSegment extends Marker implements Segment, EventContents {
	
	MarkerSegment(Marker m) {
		super(m.getName(), m.getValue());
	}

	public SegmentType getSegmentType() {
		return SegmentType.Marker;
	}
	
	public ContentType getContentType() {
		return ContentType.MARKER;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
