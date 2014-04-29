package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.tools.StateObject;


/**
 * BlockStruct is a pull interface for the first step of the layout process
 * @author Joel Håkansson
 */
class BlockStruct implements FormatterCore {
	protected final StateObject state;
	protected final FormatterContext context;
	private final Stack<BlockSequence> blocks;
	private BlockSequence currentSequence;

	
	public BlockStruct(FormatterContext context) {
		this.context = context;
		this.blocks = new Stack<BlockSequence>();
		this.state = new StateObject();
	}
		
	public void newSequence(SequenceProperties p) {
		state.assertOpen();
		currentSequence = new BlockSequence(p.getInitialPageNumber(), context.getMasters().get(p.getMasterName()));
		blocks.push(currentSequence);
	}

	public LayoutMasterBuilder newLayoutMaster(String name,
			LayoutMasterProperties properties) {
		return context.newLayoutMaster(name, properties);
	}

	public void open() {
		state.assertUnopened();
		state.open();
	}
	
	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}
	
	/**
	 * Gets the resulting data structure
	 * @return returns the data structure
	 * @throws IllegalStateException if not closed 
	 */
	public BlockStruct getFlowStruct() {
		state.assertClosed();
		return this;
	}
	
	/**
	 * Gets the block sequence interable
	 * @return returns the block sequence interable
	 */
	public Iterable<BlockSequence> getBlockSequenceIterable() {
		return blocks;
	}
	
	public void startBlock(BlockProperties props) {
		state.assertOpen();
		currentSequence.startBlock(props);
	}

	public void startBlock(BlockProperties props, String blockId) {
		state.assertOpen();
		currentSequence.startBlock(props, blockId);
	}

	public void endBlock() {
		state.assertOpen();
		currentSequence.endBlock();
	}

	public void insertMarker(Marker marker) {
		state.assertOpen();
		currentSequence.insertMarker(marker);
	}

	public void insertAnchor(String ref) {
		state.assertOpen();
		currentSequence.insertAnchor(ref);
	}

	public void insertLeader(Leader leader) {
		state.assertOpen();
		currentSequence.insertLeader(leader);
	}

	public void addChars(CharSequence chars, TextProperties props) {
		state.assertOpen();
		currentSequence.addChars(chars, props);
	}

	public void newLine() {
		state.assertOpen();
		currentSequence.newLine();
	}

	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		state.assertOpen();
		currentSequence.insertReference(identifier, numeralStyle);
	}

	public void insertEvaluate(DynamicContent exp, TextProperties t) {
		state.assertOpen();
		currentSequence.insertEvaluate(exp, t);
	}

}
