package org.daisy.dotify.formatter.obfl;

import org.daisy.dotify.formatter.dom.BlockProperties;

/**
 * Provides an interface for block events.
 *
 * @author Joel Håkansson
 */
interface BlockEvent extends Iterable<EventContents>, BlockContents {

	/**
	 * Gets properties of this block.
	 * @return returns the properties
	 */
	public BlockProperties getProperties();

}
