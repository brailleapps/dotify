package org.daisy.dotify.formatter.dom;

public interface Segment {
	enum SegmentType {Text, NewLine, Leader, Reference, Marker, Anchor};
	
	public SegmentType getSegmentType();

}
