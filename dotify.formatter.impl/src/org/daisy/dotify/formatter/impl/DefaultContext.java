package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Context;

class DefaultContext implements Context {
	private Integer currentVolume, volumeCount, metaVolume;
	
	public DefaultContext(Integer currentVolume, Integer volumeCount) {
		this.currentVolume = currentVolume;
		this.volumeCount = volumeCount;
		this.metaVolume = null;
	} 

	public Integer getCurrentVolume() {
		return currentVolume;
	}

	public Integer getVolumeCount() {
		return volumeCount;
	}

	public Integer getCurrentPage() {
		// TODO Auto-generated method stub
		return null;
	}

	public Integer getMetaVolume() {
		return metaVolume;
	}
	
	public void setMetaVolume(Integer value) {
		this.metaVolume = value;
	}

	public Integer getMetaPage() {
		// TODO Auto-generated method stub
		return null;
	}
	
}