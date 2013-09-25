package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.Leader;

class LeaderSegment extends Leader implements Segment {
	
	protected LeaderSegment(Builder builder) {
		super(builder);
	}
	
	LeaderSegment(Leader leader) {
		super(leader);
	}

	public SegmentType getSegmentType() {
		return SegmentType.Leader;
	}

}
