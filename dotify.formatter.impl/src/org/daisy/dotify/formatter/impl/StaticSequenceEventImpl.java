package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.SequenceProperties;

class StaticSequenceEventImpl extends FormatterCoreEventImpl implements SequenceEvent {
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

	public List<BlockSequence> getBlockSequences(FormatterContext context, DefaultContext c, CrossReferences crh) {
		ArrayList<BlockSequence> ib = new ArrayList<BlockSequence>();
		BlockEventHandler beh = new BlockEventHandler(context);
		beh.formatSequence(this, c);
		try {
			for (BlockSequence s : beh.close().getBlockSequenceIterable()) {
				ib.add(s);
			}
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Failed to format block.");
		}
		return ib;
	}

}
