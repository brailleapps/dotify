package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.formatter.dom.Block;
import org.daisy.dotify.formatter.dom.BlockSequence;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.SequenceProperties;


class BlockSequenceImpl extends Stack<Block> implements BlockSequence {
	private final SequenceProperties p;
	private final LayoutMaster master;
	
	public BlockSequenceImpl(SequenceProperties p, LayoutMaster master) {
		this.p = p;
		this.master = master;
	}
	/*
	public SequenceProperties getSequenceProperties() {
		return p;
	}*/

	public BlockImpl newBlock(String blockId, RowDataProperties rdp) {
		return (BlockImpl)this.push((Block)new BlockImpl(blockId, rdp));
	}
	
	public BlockImpl getCurrentBlock() {
		return (BlockImpl)this.peek();
	}
/*
	public Block[] toArray() {
		Block[] ret = new Block[this.size()];
		return super.toArray(ret);
	}*/

	private static final long serialVersionUID = -6105005856680272131L;

	public LayoutMaster getLayoutMaster() {
		return master;
	}

	public Block getBlock(int index) {
		return this.elementAt(index);
	}

	public int getBlockCount() {
		return this.size();
	}

	public Integer getInitialPageNumber() {
		return p.getInitialPageNumber();
	}

	public SequenceProperties getSequenceProperties() {
		return p;
	}

}
