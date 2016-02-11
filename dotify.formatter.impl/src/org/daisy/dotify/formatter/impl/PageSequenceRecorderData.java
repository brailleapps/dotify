package org.daisy.dotify.formatter.impl;

import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockPosition;

class PageSequenceRecorderData {
	private Stack<RowGroupSequence> dataGroups = new Stack<>();
	int keepWithNext = 0;

	PageSequenceRecorderData() {
		dataGroups = new Stack<>();
		keepWithNext = 0;
	}

	/**
	 * Creates a deep copy of the supplied instance
	 * @param template the instance to copy
	 */
	PageSequenceRecorderData(PageSequenceRecorderData template) {
		dataGroups = new Stack<>();
		for (RowGroupSequence rgs : template.dataGroups) {
			dataGroups.add(new RowGroupSequence(rgs));
		}
		keepWithNext = template.keepWithNext;
	}

	float calcSize() {
		float size = 0;
		for (RowGroupSequence rgs : dataGroups) {
			size += rgs.calcSequenceSize();
		}
		return size;
	}
	
	boolean isDataGroupsEmpty() {
		return dataGroups.isEmpty();
	}
	
	boolean isDataEmpty() {
		return (dataGroups.isEmpty()||dataGroups.peek().getGroup().isEmpty());
	}
	
	void newRowGroupSequence(BlockPosition pos, RowImpl emptyRow) {
		RowGroupSequence rgs = new RowGroupSequence(pos, emptyRow);
		dataGroups.add(rgs);
	}
	
	void addRowGroup(RowGroup rg) {
		dataGroups.peek().getGroup().add(rg);
	}
	
	List<RowGroupSequence> getRowGroupSequences() {
		return dataGroups;
	}

}
