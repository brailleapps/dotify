package org.daisy.dotify.obfl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.formatter.TextProperties;


/**
 * Provides an evaluate event object.
 * 
 * @author Joel Håkansson
 *
 */
class Evaluate implements EventContents {
	private final String expression;
	private final Map<String, String> vars;
	private final TextProperties props;
	
	public Evaluate(String expression, Map<String, String> vars, TextProperties props) {
		this.expression = expression;
		this.vars = vars;
		this.props = props;
	}
	
	public Evaluate(String expression, TextProperties props) {
		this(expression, new HashMap<String, String>(), props);
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

	public TextProperties getTextProperties() {
		return props;
	}

}
