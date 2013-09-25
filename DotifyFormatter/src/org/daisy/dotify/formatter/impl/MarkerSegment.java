package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.Marker;

class MarkerSegment extends Marker implements Segment {
	
	MarkerSegment(Marker m) {
		super(m.getName(), m.getValue());
	}

	public SegmentType getSegmentType() {
		return SegmentType.Marker;
	}

}
