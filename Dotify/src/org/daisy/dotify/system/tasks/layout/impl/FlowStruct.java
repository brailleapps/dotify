package org.daisy.dotify.system.tasks.layout.impl;

import java.util.Stack;

import org.daisy.dotify.system.tasks.layout.flow.SequenceProperties;


/**
 * FlowStruct is the data structure for the first step of the layout process
 * @author joha
 *
 */
public class FlowStruct {
	private Stack<FlowSequence> sequence;

	public FlowStruct() {
		this.sequence = new Stack<FlowSequence>();
	}
	
	public void newSequence(SequenceProperties p) {
		sequence.push(new FlowSequence(p));
	}
	
	public FlowSequence getCurrentSequence() {
		return sequence.peek();
	}
	
	/*
	public LayoutMaster getLayoutMaster(String name) {
		return masters.get(name);
	}*/

	/*
	public void newFlowGroup() {
		sequence.peek().newFlowGroup();
	}
	
	public void addSpaceBefore(int spaceBefore) {
		sequence.peek().addSpaceBefore(spaceBefore);
	}
	
	public void addSpaceAfter(int spaceAfter) {
		sequence.peek().addSpaceAfter(spaceAfter);
	}
	
	public void setBreakBeforeType(BlockProperties.BreakBeforeType breakBefore) {
		sequence.peek().setBreakBeforeType(breakBefore);
	}
	
	public SequenceProperties getCurrentSequenceProperties() {
		return sequence.peek().getSequenceProperties();
	}
	
	public void pushRow(Row row) {
		sequence.peek().pushRow(row);
	}
	
	public Row popRow() {
		return sequence.peek().popRow();
	}
	
	public void addMarker(Marker m) {
		sequence.peek().addMarker(m);
	}
	*/
	public FlowSequence[] toArray() {
		FlowSequence[] ret = new FlowSequence[sequence.size()];
		return sequence.toArray(ret);
	}

}
