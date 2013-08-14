package org.daisy.dotify.obfl;

import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.formatter.SequenceProperties;

class StaticSequenceEventImpl extends Stack<BlockEvent> implements StaticSequenceEvent {
	private final SequenceProperties props;
	
	/**
	 * Creates a new sequence event
	 * @param props
	 */
	public StaticSequenceEventImpl(SequenceProperties props) {
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
