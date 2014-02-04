package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.api.formatter.SequenceProperties;


class BlockStructImpl implements BlockStruct {

	private final Stack<BlockSequence> blocks;
	private final FormatterContext context;

	public BlockStructImpl(FormatterContext context) {
		this.context = context;
		this.blocks = new Stack<BlockSequence>();
	}
		
	public void newSequence(SequenceProperties p) {
		blocks.push((BlockSequence)new FormatterCoreImpl(p, context.getMasters().get(p.getMasterName()), context));
	}
	
	public FormatterCoreImpl getCurrentSequence() {
		return (FormatterCoreImpl)blocks.peek();
	}
	


	/*public LayoutMaster getLayoutMaster(String name) {
		return masters.get(name);
	}*/
	/*
	public HashMap<String, LayoutMaster> getMasters() {
		return masters;
	}*/

	public Iterable<BlockSequence> getBlockSequenceIterable() {
		return blocks;
	}

}
