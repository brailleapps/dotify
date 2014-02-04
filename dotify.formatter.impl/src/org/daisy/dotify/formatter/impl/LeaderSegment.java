package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Leader;

class LeaderSegment extends Leader implements Segment, EventContents {
	
	protected LeaderSegment(Builder builder) {
		super(builder);
	}
	
	LeaderSegment(Leader leader) {
		super(leader);
	}

	public SegmentType getSegmentType() {
		return SegmentType.Leader;
	}
	
	public ContentType getContentType() {
		return ContentType.LEADER;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
