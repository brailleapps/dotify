package org.daisy.dotify.formatter.dom;

/**
 * Provides an interface for block events.
 *
 * @author Joel Håkansson
 */
public interface BlockEvent extends Iterable<EventContents>, BlockContents {

	/**
	 * Gets properties of this block.
	 * @return returns the properties
	 */
	public BlockProperties getProperties();

}
