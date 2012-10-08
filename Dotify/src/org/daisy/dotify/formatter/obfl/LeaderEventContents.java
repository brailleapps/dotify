package org.daisy.dotify.formatter.obfl;

import org.daisy.dotify.formatter.dom.Leader;

class LeaderEventContents extends Leader implements EventContents {
	
	public LeaderEventContents(Builder builder) {
		super(builder);
	}

	public ContentType getContentType() {
		return ContentType.LEADER;
	}
}
