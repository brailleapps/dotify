package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Stack;

import org.daisy.dotify.formatter.dom.BlockSequence;
import org.daisy.dotify.formatter.dom.BlockStruct;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.SequenceProperties;

class BlockStructImpl implements BlockStruct {
	private final HashMap<String, LayoutMaster> masters;
	private final Stack<BlockSequence> blocks;

	public BlockStructImpl() {
		this(new HashMap<String, LayoutMaster>());
	}
	
	public BlockStructImpl(HashMap<String, LayoutMaster> masters) {
		this.masters = masters;
		this.blocks = new Stack<BlockSequence>();
	}
	
	public void newSequence(SequenceProperties p) {
		blocks.push((BlockSequence)new BlockSequenceImpl(p, masters.get(p.getMasterName())));
	}
	
	public BlockSequenceImpl getCurrentSequence() {
		return (BlockSequenceImpl)blocks.peek();
	}
	
	public void addLayoutMaster(String name, LayoutMaster master) {
		masters.put(name, master);
	}

	/*public LayoutMaster getLayoutMaster(String name) {
		return masters.get(name);
	}*/
	/*
	public HashMap<String, LayoutMaster> getMasters() {
		return masters;
	}*/
	
	private static final long serialVersionUID = 1767991496272494564L;

	public Iterable<BlockSequence> getBlockSequenceIterable() {
		return blocks;
	}

}
