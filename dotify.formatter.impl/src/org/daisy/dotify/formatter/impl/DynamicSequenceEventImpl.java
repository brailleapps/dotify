package org.daisy.dotify.formatter.impl;

import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.DynamicSequenceBuilder;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.ItemSequenceProperties;
import org.daisy.dotify.api.formatter.ReferenceListBuilder;
import org.daisy.dotify.api.formatter.SequenceProperties;

class DynamicSequenceEventImpl implements VolumeSequence, DynamicSequenceBuilder {
	private final SequenceProperties props;
	private final Stack<BlockGroup> formatters;  

	
	/**
	 * Creates a new sequence event
	 * @param props
	 */
	public DynamicSequenceEventImpl(SequenceProperties props) {
		this.props = props;
		this.formatters = new Stack<BlockGroup>();
	}

	public SequenceProperties getSequenceProperties() {
		return props;
	}

	public BlockSequence getBlockSequence(FormatterContext context, DefaultContext c, CrossReferences crh) {
		BlockSequenceManipulator fsm = new BlockSequenceManipulator(
				context.getMasters().get(getSequenceProperties().getMasterName()), 
				getSequenceProperties().getInitialPageNumber());
		boolean hasContent = false;
		for (BlockGroup b : formatters) {
			List<Block> g = b.getBlocks(context, c, crh);
			if (g.size()>0) {
				if (b.isGenerated()) {
					hasContent = true;
				}
				fsm.appendGroup(g);
			}
		}
		if (hasContent) {
			return fsm.newSequence();
		} else {
			return null;
		}
	}

	@Override
	public FormatterCore newStaticContext() {
		FormatterCoreImpl n = new FormatterCoreImpl();
		formatters.add(n);
		return n;
	}

	@Override
	public ReferenceListBuilder newReferencesListContext(ItemSequenceProperties props) {
		ItemSequenceEventImpl n = new ItemSequenceEventImpl(props.getRange(), props.getCollectionID());
		formatters.add(n);
		return n;
	}

}
