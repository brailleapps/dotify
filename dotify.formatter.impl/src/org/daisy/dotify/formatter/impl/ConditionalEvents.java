package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;


class ConditionalEvents {
	private final Condition condition;
	private final FormatterCoreEventImpl events;
	
	public ConditionalEvents(FormatterCoreEventImpl events, Condition condition) {
		this.events = events;
		this.condition = condition;
	}
	
	public Iterable<BlockEvent> getEvents() {
		return events;
	}
	
	public boolean appliesTo(Context context) {
		if (condition==null) {
			return true;
		}
		return condition.evaluate(context);
	}

}