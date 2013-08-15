package org.daisy.dotify.formatter.impl;


class NewLineSegment implements Segment {
	private final int leftIndent;
	
	public NewLineSegment(int leftIndent) {
		this.leftIndent = leftIndent;
	}
	
	public int getLeftIndent() {
		return leftIndent;
	}

	public SegmentType getSegmentType() {
		return SegmentType.NewLine;
	}

}