package org.daisy.dotify.formatter.dom;

import java.util.Map;
import java.util.Stack;



class SequenceEventImpl extends Stack<BlockEvent> implements SequenceEvent {
	private final SequenceProperties props;
	
	public SequenceEventImpl(SequenceProperties props) {
		this.props = props;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4646831324973203983L;

	public SequenceProperties getSequenceProperties() {
		return props;
	}

	public Type getType() {
		return Type.STATIC;
	}

	public void setEvaluateContext(Map<String, String> vars) {
		for (BlockEvent e : this) {
			e.setEvaluateContext(vars);
		}
	}
}
