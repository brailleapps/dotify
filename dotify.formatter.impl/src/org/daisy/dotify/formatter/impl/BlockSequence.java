package org.daisy.dotify.formatter.impl;

/**
 * Provides an interface for a sequence of block contents.
 * 
 * @author Joel Håkansson
 */
class BlockSequence extends FormatterCoreImpl {
	private final LayoutMaster master;
	private final Integer initialPagenum;
	
	public BlockSequence(Integer initialPagenum, LayoutMaster master) {
		this.initialPagenum = initialPagenum;
		this.master = master;
	}

	private static final long serialVersionUID = -6105005856680272131L;

	/**
	 * Gets the layout master for this sequence
	 * @return returns the layout master for this sequence
	 */
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

	/**
	 * Get the initial page number, i.e. the number that the first page in the sequence should have
	 * @return returns the initial page number, or null if no initial page number has been specified
	 */
	public Integer getInitialPageNumber() {
		return initialPagenum;
	}
	
	/**
	 * Gets the minimum number of rows that the specified block requires to begin 
	 * rendering on a page.
	 * 
	 * @param block the block to get the 
	 * @param refs
	 * @return the minimum number of rows
	 */
	public int getKeepHeight(Block block) {
		return getKeepHeight(this.indexOf(block));
	}
	private int getKeepHeight(int gi) {
		//FIXME: this assumes that row spacing is equal to 1
		int keepHeight = getBlock(gi).getSpaceBefore()+getBlock(gi).getBlockContentManager().getRowCount();
		if (getBlock(gi).getKeepWithNext()>0 && gi+1<getBlockCount()) {
			keepHeight += getBlock(gi).getSpaceAfter()+getBlock(gi+1).getSpaceBefore()+getBlock(gi).getKeepWithNext();
			switch (getBlock(gi+1).getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(gi+1);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}


}
