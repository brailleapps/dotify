package org.daisy.dotify.obfl;

import org.daisy.dotify.api.formatter.Leader;

class LeaderEventContents extends Leader implements EventContents {
	
	public LeaderEventContents(Builder builder) {
		super(builder);
	}

	public ContentType getContentType() {
		return ContentType.LEADER;
	}
}
