package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.SequenceProperties;


abstract class BlockSequenceImpl extends Stack<Block> implements BlockSequence {
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

	public BlockImpl newBlock(String blockId, RowDataProperties.Builder rdp) {
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

	private Block getBlock(int index) {
		return this.elementAt(index);
	}

	private int getBlockCount() {
		return this.size();
	}

	public Integer getInitialPageNumber() {
		return p.getInitialPageNumber();
	}

	public SequenceProperties getSequenceProperties() {
		return p;
	}
	
	public int getKeepHeight(Block block, CrossReferences refs) {
		return getKeepHeight(this.indexOf(block), refs);
	}
	private int getKeepHeight(int gi, CrossReferences refs) {
		int keepHeight = getBlock(gi).getSpaceBefore()+getBlock(gi).getBlockContentManager(refs).getRowCount();
		if (getBlock(gi).getKeepWithNext()>0 && gi+1<getBlockCount()) {
			keepHeight += getBlock(gi).getSpaceAfter()+getBlock(gi+1).getSpaceBefore()+getBlock(gi).getKeepWithNext();
			switch (getBlock(gi+1).getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(gi+1, refs);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}


}
