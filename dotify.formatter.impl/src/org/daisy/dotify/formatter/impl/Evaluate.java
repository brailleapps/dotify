package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.TextProperties;


/**
 * Provides an evaluate event object.
 * 
 * @author Joel Håkansson
 *
 */
class Evaluate implements EventContents {
	private final String expression;
	private final TextProperties props;
	
	public Evaluate(String expression, TextProperties props) {
		this.expression = expression;
		this.props = props;
	}
	
	public String getExpression() {
		return expression;
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
