package org.daisy.dotify.formatter.impl;

import java.util.List;

import org.daisy.dotify.api.formatter.SequenceProperties;

/**
 * Provides a volume sequence object. A volume sequence is a chunk of contents
 * that is to be placed before or after the contents of a volume.
 * 
 * @author Joel Håkansson
 */
interface VolumeSequence {
	/**
	 * Gets the volume sequence properties.
	 * @return returns the volume sequence properties
	 */
	public SequenceProperties getSequenceProperties();
	
	public List<BlockSequence> getBlockSequence(FormatterContext context, DefaultContext c, CrossReferences crh);
}
