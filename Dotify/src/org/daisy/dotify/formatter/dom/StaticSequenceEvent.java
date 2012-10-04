package org.daisy.dotify.formatter.dom;

import java.util.Map;
import java.util.Stack;


/**
 * Provides a static sequence event object.
 * 
 * @author Joel Håkansson
 */
public class StaticSequenceEvent extends Stack<BlockEvent> implements SequenceEvent {
	private final SequenceProperties props;
	
	/**
	 * Creates a new sequence event
	 * @param props
	 */
	public StaticSequenceEvent(SequenceProperties props) {
		this.props = props;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 4646831324973203983L;

	public SequenceProperties getSequenceProperties() {
		return props;
	}

	public VolumeSequenceType getVolumeSequenceType() {
		return VolumeSequenceType.STATIC;
	}

	public void setEvaluateContext(Map<String, String> vars) {
		for (BlockEvent e : this) {
			e.setEvaluateContext(vars);
		}
	}
}
