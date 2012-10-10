package org.daisy.dotify.obfl;



interface VolumeTemplate {

	/**
	 * Test if this Template applies to this combination of volume and volume count.
	 * @param volume the volume to test
	 * @return returns true if the Template should be applied to the volume
	 */
	public boolean appliesTo(int volume, int volumeCount);
	
	public Iterable<VolumeSequenceEvent> getPreVolumeContent();
	
	public Iterable<VolumeSequenceEvent> getPostVolumeContent();
	
	public String getVolumeNumberVariableName();
	public String getVolumeCountVariableName();
	
	/**
	 * Gets the maximum number of sheets allowed.
	 * @return returns the number of sheets allowed
	 */
	public int getVolumeMaxSize();
}
