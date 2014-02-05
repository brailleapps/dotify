package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;



interface VolumeTemplate extends VolumeTemplateBuilder {

	/**
	 * Test if this Template applies to this combination of volume and volume count.
	 * @param volume the volume to test
	 * @return returns true if the Template should be applied to the volume
	 */
	public boolean appliesTo(Context context);
	
	public Iterable<VolumeSequenceEvent> getPreVolumeContent();
	
	public Iterable<VolumeSequenceEvent> getPostVolumeContent();
	
	/**
	 * Gets the maximum number of sheets allowed.
	 * @return returns the number of sheets allowed
	 */
	public int getVolumeMaxSize();
}
