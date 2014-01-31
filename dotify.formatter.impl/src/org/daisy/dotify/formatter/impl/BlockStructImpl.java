package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;


class BlockStructImpl implements BlockStruct {
	private final HashMap<String, LayoutMaster> masters;
	private final Stack<BlockSequence> blocks;
	private final BrailleTranslator bt;

	public BlockStructImpl(BrailleTranslator bt) {
		this(new HashMap<String, LayoutMaster>(), bt);
	}
	
	public BlockStructImpl(HashMap<String, LayoutMaster> masters, BrailleTranslator bt) {
		this.masters = masters;
		this.blocks = new Stack<BlockSequence>();
		this.bt = bt;
	}
	
	public void newSequence(SequenceProperties p) {
		blocks.push((BlockSequence)new FormatterCoreImpl(p, masters.get(p.getMasterName()), bt));
	}
	
	public FormatterCoreImpl getCurrentSequence() {
		return (FormatterCoreImpl)blocks.peek();
	}
	
	public void addLayoutMaster(String name, LayoutMaster master) {
		masters.put(name, master);
	}
	
	public Map<String, LayoutMaster> getMasters() {
		return masters;
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
