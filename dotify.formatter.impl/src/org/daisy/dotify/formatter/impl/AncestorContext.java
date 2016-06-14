package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.BlockProperties;

class AncestorContext {
	private final BlockProperties blockProperties;
	private int listIterator;
	
	AncestorContext(BlockProperties props) {
		this.blockProperties = props;
		this.listIterator = 0;
	}
	public BlockProperties getBlockProperties() {
		return blockProperties;
	}

	public int nextListNumber() {
		listIterator++;
		return listIterator;
	}
}