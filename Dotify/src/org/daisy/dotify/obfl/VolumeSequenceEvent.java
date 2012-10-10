package org.daisy.dotify.obfl;

import org.daisy.dotify.formatter.SequenceProperties;

/**
 * Provides a volume sequence event object. A volume sequence is a chunk of contents
 * that is to be placed before or after the contents of a volume.
 * 
 * @author Joel Håkansson
 */
interface VolumeSequenceEvent {
	/**
	 * Defines types of volume sequences
	 */
	enum VolumeSequenceType {STATIC, TABLE_OF_CONTENTS};
	/**
	 * Gets the volume sequence event type.
	 * @return returns the volume sequence event type
	 */
	//public VolumeSequenceType getVolumeSequenceType();
	/**
	 * Gets the volume sequence event properties.
	 * @return returns the volume sequence event properties
	 */
	public SequenceProperties getSequenceProperties();
}
