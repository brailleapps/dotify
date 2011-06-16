package org.daisy.dotify.formatter;

public interface BlockEvent extends Iterable<BlockContents>, BlockContents {

	
	public BlockProperties getProperties();
}
