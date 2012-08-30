package org.daisy.dotify.formatter.dom;

public class AnchorSegment implements Segment {
	private final String referenceID;
	
	public AnchorSegment(String referenceID) {
		this.referenceID = referenceID;
	}

	public SegmentType getSegmentType() {
		return SegmentType.Anchor;
	}

	public String getReferenceID() {
		return referenceID;
	}

}
