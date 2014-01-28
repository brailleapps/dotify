package org.daisy.dotify.obfl;

import org.daisy.dotify.api.formatter.BlockProperties;

/**
 * Provides an interface for block events.
 *
 * @author Joel Håkansson
 */
interface BlockEvent extends BlockContents {

	/**
	 * Gets properties of this block.
	 * @return returns the properties
	 */
	public BlockProperties getProperties();

	public String getBlockId();

}
