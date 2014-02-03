package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;



class BlockEventImpl extends Stack<EventContents> implements BlockEvent {
	private final BlockProperties props;
	private final String blockId;

	public BlockEventImpl(BlockProperties props) {
		this(props, null);
	}
	
	public BlockEventImpl(BlockProperties props, String blockId) {
		this.props = props;
		this.blockId = blockId;
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 9098524584205247145L;

	public ContentType getContentType() {
		return ContentType.BLOCK;
	}
	
	public BlockProperties getProperties() {
		return props;
	}

	public String getBlockId() {
		return blockId;
	}

	public boolean canContainEventObjects() {
		return true;
	}


}
