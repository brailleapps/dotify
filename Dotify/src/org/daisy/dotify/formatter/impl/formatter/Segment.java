package org.daisy.dotify.formatter.impl.formatter;

interface Segment {
	enum SegmentType {Text, NewLine, Leader, Reference, Marker, Anchor};
	
	public SegmentType getSegmentType();

}