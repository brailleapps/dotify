package org.daisy.dotify.obfl;



/**
 * Provides the toc events for a specified context
 * @author Joel Håkansson
 *
 */
interface TocEvents {
	
	/**
	 * Gets the events that should precede the TOC
	 * @return returns the events that should precede the TOC
	 */
	public Iterable<BlockEvent> getTocStartEvents();
	
	/**
	 * Gets the events that should precede TOC entries from the specified volume 
	 * @param forVolume the number of the volume that is to be started, one based
	 * @return returns the events that should precede the TOC entries from the specified volume
	 */
	public Iterable<BlockEvent> getVolumeStartEvents(int forVolume);

	/**
	 * Gets the events that should follow TOC entries from the specified volume
	 * @param forVolume the number of the volume that has just ended, one based
	 * @return returns the events that should follow the TOC entries from the specified volume
	 */
	public Iterable<BlockEvent> getVolumeEndEvents(int forVolume);
	
	/**
	 * Gets the events that should follow the TOC
	 * @return returns the events that should follow the TOC
	 */
	public Iterable<BlockEvent> getTocEndEvents();

}
