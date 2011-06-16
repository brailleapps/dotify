package org.daisy.dotify.formatter;

public class Evaluate implements BlockContents {
	private final String expression;
	
	public Evaluate(String expression) {
		this.expression = expression;
	}
	
	public String getExpression() {
		return expression;
	}

	public ContentType getContentType() {
		return ContentType.EVALUATE;
	}

}
