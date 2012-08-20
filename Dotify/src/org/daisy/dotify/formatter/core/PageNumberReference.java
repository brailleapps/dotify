package org.daisy.dotify.formatter.core;

import org.daisy.dotify.formatter.core.NumeralField.NumeralStyle;
import org.daisy.dotify.formatter.dom.EventContents;
import org.daisy.dotify.formatter.dom.Segment;


public class PageNumberReference implements EventContents, Segment {
	private final String refid;
	private final NumeralStyle style;
	
	public PageNumberReference(String refid, NumeralStyle style) {
		this.refid = refid;
		this.style = style;
	}

	public ContentType getContentType() {
		return ContentType.PAGE_NUMBER;
	}
	
	public SegmentType getSegmentType() {
		return SegmentType.Reference;
	}
	
	public String getRefId() {
		return refid;
	}
	
	public NumeralStyle getNumeralStyle() {
		return style;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
