package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.TextProperties;


/**
 * Provides an evaluate event object.
 * 
 * @author Joel Håkansson
 *
 */
class Evaluate implements Segment {
	private final DynamicContent expression;
	private final TextProperties props;
	
	public Evaluate(DynamicContent expression, TextProperties props) {
		this.expression = expression;
		this.props = props;
	}
	
	public DynamicContent getExpression() {
		return expression;
	}

	public TextProperties getTextProperties() {
		return props;
	}

	public SegmentType getSegmentType() {
		return SegmentType.Evaluate;
	}

}
