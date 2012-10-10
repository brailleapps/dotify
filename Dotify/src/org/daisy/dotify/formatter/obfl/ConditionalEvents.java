package org.daisy.dotify.formatter.obfl;

import java.util.Map;


class ConditionalEvents {
	private final String condition;
	private final Iterable<BlockEvent> events;
	
	public ConditionalEvents(Iterable<BlockEvent> events, String condition) {
		this.events = events;
		this.condition = condition;
	}
	
	public Iterable<BlockEvent> getEvents() {
		return events;
	}
	
	public boolean appliesTo(Map<String, String> variables) {
		if (condition==null) {
			return true;
		}
		return new Expression().evaluate(condition, variables).equals(true);
	}

}