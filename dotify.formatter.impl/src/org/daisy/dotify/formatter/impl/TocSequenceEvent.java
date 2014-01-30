package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.TocProperties;


/**
 * Provides a TOC sequence event object.
 * 
 * @author Joel Håkansson
 */
interface TocSequenceEvent extends VolumeSequenceEvent {

	public String getTocName();

	public TocProperties.TocRange getRange();
	
	/**
	 * Returns true if this toc sequence applies to the supplied context
	 * @param volume
	 * @param volumeCount
	 * @return returns true if this toc sequence applies to the supplied context, false otherwise
	 */
	public boolean appliesTo(int volume, int volumeCount);

	/**
	 * Gets the TOC events 
	 * @param volume
	 * @param volumeCount
	 * @return returns the TOC events
	 */
	public TocEvents getTocEvents(int volume, int volumeCount);

}