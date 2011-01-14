package org.daisy.dotify.system.tasks.layout.impl;

import java.util.Stack;

import org.daisy.dotify.system.tasks.layout.flow.SequenceProperties;


public class FlowSequence {
	private Stack<FlowGroup> group;
	private SequenceProperties p;
	
	public FlowSequence(SequenceProperties p) {
		this.group = new Stack<FlowGroup>();
		this.p = p;
	}
	
	public SequenceProperties getSequenceProperties() {
		return p;
	}

	public FlowGroup newFlowGroup() {
		FlowGroup ret = new FlowGroup();
		group.push(ret);
		return ret;
	}
	
	public FlowGroup getCurrentGroup() {
		return group.peek();
	}

	/*
	public void addSpaceBefore(int spaceBefore) {
		group.peek().addSpaceBefore(spaceBefore);
	}
	
	public void addSpaceAfter(int spaceAfter) {
		group.peek().addSpaceAfter(spaceAfter);
	}
	
	public void setBreakBeforeType(BlockProperties.BreakBeforeType breakBefore) {
		group.peek().setBreakBeforeType(breakBefore);
	}
	
	public void pushRow(Row row) {
		group.peek().pushRow(row);
	}
	
	public Row popRow() {
		return group.peek().popRow();
	}
	
	public void addMarker(Marker m) {
		group.peek().addMarker(m);
	}*/
	
	public FlowGroup[] toArray() {
		FlowGroup[] ret = new FlowGroup[group.size()];
		return group.toArray(ret);
	}

}
