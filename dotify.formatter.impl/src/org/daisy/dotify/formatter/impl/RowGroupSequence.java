package org.daisy.dotify.formatter.impl;

import java.util.List;

import org.daisy.dotify.api.formatter.BlockPosition;

class RowGroupSequence {
	private final List<RowGroup> group;
	private final BlockPosition pos;
	private final RowImpl emptyRow;

	public RowGroupSequence(List<RowGroup> group) {
		this(group, null, null);
	}

	public RowGroupSequence(List<RowGroup> group, BlockPosition pos, RowImpl emptyRow) {
		this.group = group;
		this.pos = pos;
		this.emptyRow = emptyRow;
	}

	public List<RowGroup> getGroup() {
		return group;
	}

	public BlockPosition getBlockPosition() {
		return pos;
	}

	public RowImpl getEmptyRow() {
		return emptyRow;
	}

}
