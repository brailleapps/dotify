package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.SequenceProperties;

class StaticSequenceEventImpl extends FormatterCoreImpl implements VolumeSequence {
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

	public List<BlockSequence> getBlockSequence(FormatterContext context, DefaultContext c, CrossReferences crh) {
		BlockSequenceManipulator fsm = new BlockSequenceManipulator(
				context.getMasters().get(getSequenceProperties().getMasterName()), 
				getSequenceProperties().getInitialPageNumber());
		fsm.appendGroup(this);
		ArrayList<BlockSequence> ret = new ArrayList<BlockSequence>();
		ret.add(fsm.newSequence());
		return ret;
	}

}
