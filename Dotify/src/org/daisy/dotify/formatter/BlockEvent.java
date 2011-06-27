package org.daisy.dotify.formatter;


public interface BlockEvent extends Iterable<EventContents>, BlockContents {

	public BlockProperties getProperties();

}
