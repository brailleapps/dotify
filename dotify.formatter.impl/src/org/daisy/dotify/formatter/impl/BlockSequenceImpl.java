package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.LayoutMaster;
class BlockSequenceImpl extends FormatterCoreImpl implements BlockSequence {
	private final LayoutMaster master;
	private final Integer initialPagenum;
	
	public BlockSequenceImpl(Integer initialPagenum, LayoutMaster master) {
		this.initialPagenum = initialPagenum;
		this.master = master;
	}

	private static final long serialVersionUID = -6105005856680272131L;

	public LayoutMaster getLayoutMaster() {
		return master;
	}

	/**
	 * Gets the block with the specified index, where index >= 0 && index < getBlockCount()
	 * @param index the block index
	 * @return returns the block index
	 * @throws IndexOutOfBoundsException if index < 0 || index >= getBlockCount()
	 */
	private Block getBlock(int index) {
		return this.elementAt(index);
	}

	/**
	 * Gets the number of blocks in this sequence
	 * @return returns the number of blocks in this sequence
	 */
	private int getBlockCount() {
		return this.size();
	}

	public Integer getInitialPageNumber() {
		return initialPagenum;
	}
	
	public int getKeepHeight(Block block, CrossReferences refs, Context context, FormatterContext fcontext) {
		return getKeepHeight(this.indexOf(block), refs, context, fcontext);
	}
	private int getKeepHeight(int gi, CrossReferences refs, Context context, FormatterContext fcontext) {
		int keepHeight = getBlock(gi).getSpaceBefore()+getBlock(gi).getBlockContentManager(getLayoutMaster().getFlowWidth(), refs, context, fcontext).getRowCount();
		if (getBlock(gi).getKeepWithNext()>0 && gi+1<getBlockCount()) {
			keepHeight += getBlock(gi).getSpaceAfter()+getBlock(gi+1).getSpaceBefore()+getBlock(gi).getKeepWithNext();
			switch (getBlock(gi+1).getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(gi+1, refs, context, fcontext);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}


}
