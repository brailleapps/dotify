package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.NumeralStyle;

class PageNumberReferenceSegment extends PageNumberReference implements Segment, EventContents {
	
	public PageNumberReferenceSegment(String refid, NumeralStyle style) {
		super(refid, style);
	}

	public boolean canContainEventObjects() {
		return false;
	}

	public SegmentType getSegmentType() {
		return SegmentType.Reference;
	}
	
	public ContentType getContentType() {
		return ContentType.PAGE_NUMBER;
	}

}
