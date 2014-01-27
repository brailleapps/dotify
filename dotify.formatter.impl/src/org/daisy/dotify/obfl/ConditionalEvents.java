package org.daisy.dotify.obfl;

import java.util.Map;

import org.daisy.dotify.api.obfl.ExpressionFactory;


class ConditionalEvents {
	private final String condition;
	private final FormatterCoreEventImpl events;
	private final ExpressionFactory ef;
	
	public ConditionalEvents(FormatterCoreEventImpl events, String condition, ExpressionFactory ef) {
		this.events = events;
		this.condition = condition;
		this.ef = ef;
	}
	
	public Iterable<BlockEvent> getEvents() {
		return events;
	}
	
	public boolean appliesTo(Map<String, String> variables) {
		if (condition==null) {
			return true;
		}
		return ef.newExpression().evaluate(condition, variables).equals(true);
	}

}