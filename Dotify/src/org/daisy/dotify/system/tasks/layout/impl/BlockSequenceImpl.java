package org.daisy.dotify.system.tasks.layout.impl;

import java.util.Stack;

import org.daisy.dotify.system.tasks.layout.flow.Block;
import org.daisy.dotify.system.tasks.layout.flow.BlockSequence;
import org.daisy.dotify.system.tasks.layout.flow.SequenceProperties;

public class BlockSequenceImpl extends Stack<Block> implements BlockSequence {
	private SequenceProperties p;
	
	public BlockSequenceImpl(SequenceProperties p) {
		this.p = p;
	}
	
	public SequenceProperties getSequenceProperties() {
		return p;
	}

	public BlockImpl newFlowGroup() {
		return (BlockImpl)this.push((Block)new BlockImpl());
	}
	
	public BlockImpl getCurrentGroup() {
		return (BlockImpl)this.peek();
	}

	public Block[] toArray() {
		Block[] ret = new Block[this.size()];
		return super.toArray(ret);
	}

	private static final long serialVersionUID = -6105005856680272131L;

}
