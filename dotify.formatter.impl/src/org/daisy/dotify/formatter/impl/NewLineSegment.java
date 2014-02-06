package org.daisy.dotify.formatter.impl;



class NewLineSegment implements Segment, EventContents {
	
	public NewLineSegment() {
	}

	public SegmentType getSegmentType() {
		return SegmentType.NewLine;
	}
	
	public ContentType getContentType() {
		return ContentType.BR;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}