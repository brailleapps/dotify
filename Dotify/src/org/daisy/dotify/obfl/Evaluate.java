package org.daisy.dotify.obfl;

import java.util.HashMap;
import java.util.Map;



/**
 * Provides an evaluate event object.
 * 
 * @author Joel Håkansson
 *
 */
class Evaluate implements EventContents {
	private final String expression;
	private final Map<String, String> vars;
	
	public Evaluate(String expression, Map<String, String> vars) {
		this.expression = expression;
		this.vars = vars;
	}
	
	public Evaluate(String expression) {
		this(expression, new HashMap<String, String>());
	}
	
	public String getExpression() {
		return expression;
	}
	
	public Map<String, String> getVariables() {
		return vars;
	}

	public ContentType getContentType() {
		return ContentType.EVALUATE;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
