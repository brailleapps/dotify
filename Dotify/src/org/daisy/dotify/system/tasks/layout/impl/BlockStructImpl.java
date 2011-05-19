package org.daisy.dotify.system.tasks.layout.impl;

import java.util.HashMap;
import java.util.Stack;

import org.daisy.dotify.formatter.BlockSequence;
import org.daisy.dotify.formatter.BlockStruct;
import org.daisy.dotify.formatter.SequenceProperties;
import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;

public class BlockStructImpl extends Stack<BlockSequence> implements BlockStruct {
	
	private final HashMap<String, LayoutMaster> masters;

	public BlockStructImpl() {
		this.masters = new HashMap<String, LayoutMaster>();
	}
	
	public BlockStructImpl(HashMap<String, LayoutMaster> masters) {
		this.masters = masters;
	}
	
	public void newSequence(SequenceProperties p) {
		this.push((BlockSequence)new BlockSequenceImpl(p));
	}
	
	public BlockSequenceImpl getCurrentSequence() {
		return (BlockSequenceImpl)this.peek();
	}
	
	public void addLayoutMaster(String name, LayoutMaster master) {
		masters.put(name, master);
	}

	public LayoutMaster getLayoutMaster(String name) {
		return masters.get(name);
	}
	
	public HashMap<String, LayoutMaster> getMasters() {
		return masters;
	}
	
	private static final long serialVersionUID = 1767991496272494564L;

}
