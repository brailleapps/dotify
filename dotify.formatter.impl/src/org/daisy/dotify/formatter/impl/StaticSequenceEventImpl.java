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

	public VolumeSequenceType getVolumeSequenceType() {
		return VolumeSequenceType.STATIC;
	}

	public List<Iterable<BlockSequence>> getBlockSequences(FormatterContext context, DefaultContext c, CrossReferences crh) {
		ArrayList<Iterable<BlockSequence>> ib = new ArrayList<Iterable<BlockSequence>>();
		BlockEventHandler beh = new BlockEventHandler(context);
		beh.formatSequence(this, c);
		try {
			ib.add(beh.close().getBlockSequenceIterable());
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).warning("Failed to format block.");
		}
		return ib;
	}

}
