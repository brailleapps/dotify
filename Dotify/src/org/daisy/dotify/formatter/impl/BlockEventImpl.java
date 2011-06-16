package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.formatter.BlockContents;
import org.daisy.dotify.formatter.BlockEvent;
import org.daisy.dotify.formatter.BlockProperties;

public class BlockEventImpl extends Stack<BlockContents> implements BlockEvent {
	private final BlockProperties props;

	public BlockEventImpl(BlockProperties props) {
		this.props = props;
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


}
