package org.daisy.dotify.obfl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.paginator.Block;
import org.daisy.dotify.paginator.BlockSequence;
import org.daisy.dotify.paginator.BlockStruct;

/**
 * Provides methods for manipulating a flow sequence.
 * @author Joel Håkansson
 */
class BlockSequenceManipulator {
	private HashMap<String, Integer> taggedEntries;
	private final Stack<Block> sequence;
	//private final SequenceProperties props;
	private int initialPagenum;
	private final LayoutMaster master;
	
	public BlockSequenceManipulator(BlockStruct struct) {
		this.sequence = new Stack<Block>();
		//SequenceProperties tmp = null;
		LayoutMaster tMaster = null;
		for (BlockSequence b : struct.getBlockSequenceIterable()) {
			//tmp = b.getSequenceProperties();
			initialPagenum = b.getInitialPageNumber();
			tMaster = b.getLayoutMaster();
			for (Block bb : b) {
				this.sequence.add(bb);
			}
		}
		//this.props = tmp;
		this.master = tMaster;
		this.taggedEntries = tagSequence(this.sequence);
	}

	private BlockSequence newSequence(List<Block> c) {
		BlockSeqImpl ret = new BlockSeqImpl(initialPagenum, master);
		ret.addAll(c);
		return ret;
	}
	
	public BlockSequence newSequence() {
		return newSequence(sequence);
	}
	
	/*
	public BlockSequence newSubSequence(String fromId) {
		Integer fromIndex = taggedEntries.get(fromId);
		if (fromIndex==null) {
			throw new IllegalArgumentException("Cannot find identifier " + fromId);
		}
		return newSequence(sequence.subList(fromIndex, sequence.size()));
	}*/
	
	public void insertGroup(Iterable<Block> blocks, String beforeId) {
		ArrayList<Block> call = new ArrayList<Block>();
		for (Block b : blocks) {
			call.add(b);
		}
		insertGroup(call, beforeId);
	}
	public void appendGroup(Iterable<Block> blocks) {
		ArrayList<Block> call = new ArrayList<Block>();
		for (Block b : blocks) {
			call.add(b);
		}
		sequence.addAll(call);
		taggedEntries = tagSequence(sequence);
	}
	
	public void insertGroup(Collection<Block> seq, String beforeId) {
		Integer beforeIndex = taggedEntries.get(beforeId);
		if (beforeIndex==null) {
			throw new IllegalArgumentException("Cannot find identifier " + beforeId);
		}
		sequence.addAll(beforeIndex, seq);
		taggedEntries = tagSequence(sequence);
	}

	public void removeGroup(String id) {
		Integer index = taggedEntries.get(id);
		if (index==null) {
			throw new IllegalArgumentException("Cannot find identifier " + id);
		}
		sequence.removeElementAt(index);
		taggedEntries = tagSequence(sequence);
	}
	
	public void removeRange(String fromId, String toId) {
		Integer fromIndex = taggedEntries.get(fromId);
		Integer toIndex = taggedEntries.get(toId);
		if (fromIndex==null || toIndex==null) {
			throw new IllegalArgumentException("Cannot find identifier " + fromId + "/" + toId);
		}
		for (int i=0; i<toIndex-fromIndex; i++) {
			sequence.remove((int)fromIndex);
		}
		taggedEntries = tagSequence(sequence);
	}
	
	public void removeTail(String fromId) {
		Integer fromIndex = taggedEntries.get(fromId);
		fromIndex++;
		int count = sequence.size()-fromIndex;
		for (int i=0; i<count; i++) {
			sequence.remove((int)fromIndex);
		}
		taggedEntries = tagSequence(sequence);
	}

	/*
	public BlockSequence newSubSequence(String fromId, String toId) {
		Integer fromIndex = taggedEntries.get(fromId);
		Integer toIndex = taggedEntries.get(toId);
		if (fromIndex==null || toIndex==null) {
			throw new IllegalArgumentException("Cannot find identifier " + fromId + "/" + toId);
		}
		toIndex++;
		// subList handles checking of fromIndex>toIndex
		return newSequence(sequence.subList(fromIndex, toIndex));
	}*/
	/*
	public BlockSequence newFromItem(String id) {
		return newSubSequence(id, id);
	}
	*/
	private static HashMap<String, Integer> tagSequence(List<Block> seq) {
		HashMap<String, Integer> entries = new HashMap<String, Integer>();
		int i = 0;
		for (Block group : seq) {
			if (group.getBlockIdentifier()!=null && !group.getBlockIdentifier().equals("")) {
				if (entries.put(group.getBlockIdentifier(), i)!=null) {
					throw new IllegalArgumentException("Duplicate id " + group.getBlockIdentifier());
				}
				//System.out.println("GROUP! " + fg.getIdentifier());
			}
			i++;
		}
		return entries;
	}
	
	public List<Block> getBlocks() {
		return sequence;
	}
	
	private static class BlockSeqImpl extends Stack<Block> implements BlockSequence {
		/**
		 * 
		 */
		private static final long serialVersionUID = -7098716884005865317L;
		//private final SequenceProperties p;
		private final LayoutMaster master;
		private final int initialPagenum;
		
		private BlockSeqImpl(int initialPagenum, LayoutMaster master) {
			//this.p = p;
			this.initialPagenum = initialPagenum;
			this.master = master;
		}

		public LayoutMaster getLayoutMaster() {
			return master;
		}

		public Integer getInitialPageNumber() {
			return initialPagenum;
		}

		public int getBlockCount() {
			return this.size();
		}

		public Block getBlock(int index) {
			return this.elementAt(index);
		}
/*
		public SequenceProperties getSequenceProperties() {
			return p;
		}*/
	}

}
