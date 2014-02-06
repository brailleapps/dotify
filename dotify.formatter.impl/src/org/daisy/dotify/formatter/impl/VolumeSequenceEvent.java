package org.daisy.dotify.formatter.impl;

import java.util.List;

import org.daisy.dotify.api.formatter.SequenceProperties;

/**
 * Provides a volume sequence event object. A volume sequence is a chunk of contents
 * that is to be placed before or after the contents of a volume.
 * 
 * @author Joel Håkansson
 */
interface VolumeSequenceEvent {
	/**
	 * Gets the volume sequence event properties.
	 * @return returns the volume sequence event properties
	 */
	public SequenceProperties getSequenceProperties();
	
	public List<BlockSequence> getBlockSequence(FormatterContext context, DefaultContext c, CrossReferences crh);
}
