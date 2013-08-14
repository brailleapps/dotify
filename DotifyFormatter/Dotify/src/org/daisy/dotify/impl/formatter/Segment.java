package org.daisy.dotify.impl.formatter;

interface Segment {
	enum SegmentType {Text, NewLine, Leader, Reference, Marker, Anchor};
	
	public SegmentType getSegmentType();

}