package org.daisy.dotify.formatter.impl;

import java.io.IOException;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.TocProperties;


/**
 * Provides a TOC sequence event object.
 * 
 * @author Joel Håkansson
 */
interface TocSequenceEvent extends VolumeSequenceEvent{

	public String getTocName();

	public TocProperties.TocRange getRange();
	
	/**
	 * Returns true if this toc sequence applies to the supplied context
	 * @param volume
	 * @param volumeCount
	 * @return returns true if this toc sequence applies to the supplied context, false otherwise
	 */
	public boolean appliesTo(Context context);

	public String getStartedVolumeVariableName();
	
	/**
	 * Gets the events that should precede the TOC
	 * @return returns the events that should precede the TOC
	 */
	public Iterable<BlockEvent> getTocStartEvents(Context vars);
	
	/**
	 * Gets the events that should precede TOC entries from the specified volume 
	 * @param forVolume the number of the volume that is to be started, one based
	 * @return returns the events that should precede the TOC entries from the specified volume
	 */
	public Iterable<BlockEvent> getVolumeStartEvents(Context vars);

	/**
	 * Gets the events that should follow TOC entries from the specified volume
	 * @param forVolume the number of the volume that has just ended, one based
	 * @return returns the events that should follow the TOC entries from the specified volume
	 */
	public Iterable<BlockEvent> getVolumeEndEvents(Context vars);
	
	/**
	 * Gets the events that should follow the TOC
	 * @return returns the events that should follow the TOC
	 */
	public Iterable<BlockEvent> getTocEndEvents(Context vars);
	
	public BlockSequence getTocEnd(Context vars, FormatterContext context) throws IOException;

}