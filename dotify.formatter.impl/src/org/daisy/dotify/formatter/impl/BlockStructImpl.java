package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.tools.StateObject;

class BlockStructImpl implements BlockStruct, FormatterCore {
	protected final StateObject state;
	protected final FormatterContext context;
	private final Stack<BlockSequence> blocks;
	private FormatterCoreImpl currentSequence;

	public BlockStructImpl(FormatterContext context) {
		this.context = context;
		this.blocks = new Stack<BlockSequence>();
		this.state = new StateObject();
	}
		
	public void newSequence(SequenceProperties p) {
		state.assertOpen();
		currentSequence = new FormatterCoreImpl(p, context.getMasters().get(p.getMasterName()), context);
		blocks.push(currentSequence);
	}

	public void addLayoutMaster(String name, LayoutMaster master) {
		context.addLayoutMaster(name, master);
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

	public void startFloat(String id) {
		state.assertOpen();
		currentSequence.startFloat(id);
	}

	public void endFloat() {
		state.assertOpen();
		currentSequence.endFloat();
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
