package org.daisy.dotify.formatter.impl;


class NewLineSegment implements Segment {
	private final MarginProperties leftIndent;
	
	public NewLineSegment(MarginProperties leftIndent) {
		this.leftIndent = leftIndent;
	}
	
	public MarginProperties getLeftIndent() {
		return leftIndent;
	}

	public SegmentType getSegmentType() {
		return SegmentType.NewLine;
	}

}