package org.daisy.dotify.formatter.impl;

interface VolumeSplitter {

	void resetSheetCount(int sheets);
	
	void updateSheetCount(int sheets);
	
	/**
	 * Gets the number of volumes.
	 * @return returns the number of volumes
	 */
	int getVolumeCount();
	
	/**
	 * Gets the number of sheets in a volume
	 * @param volIndex volume index, one-based
	 * @return returns the number of sheets in the volume
	 */
	public int sheetsInVolume(int volIndex);
	
	void adjustVolumeCount(int sheets);
	void setReformatSplitterMax(int reformat);
	void setSplitterMax(int splitterMax);
	int getSplitterMax();
}
