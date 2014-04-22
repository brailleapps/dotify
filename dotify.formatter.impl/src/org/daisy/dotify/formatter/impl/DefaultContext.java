package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Context;

class DefaultContext implements Context {
	private Integer currentVolume, volumeCount, metaVolume, metaPage;
	
	public DefaultContext(Integer currentVolume, Integer volumeCount) {
		this.currentVolume = currentVolume;
		this.volumeCount = volumeCount;
		this.metaVolume = null;
		this.metaPage = null;
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
		return metaPage;
	}

	public void setMetaPage(Integer metaPage) {
		this.metaPage = metaPage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currentVolume == null) ? 0 : currentVolume.hashCode());
		result = prime * result
				+ ((metaPage == null) ? 0 : metaPage.hashCode());
		result = prime * result
				+ ((metaVolume == null) ? 0 : metaVolume.hashCode());
		result = prime * result
				+ ((volumeCount == null) ? 0 : volumeCount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultContext other = (DefaultContext) obj;
		if (currentVolume == null) {
			if (other.currentVolume != null)
				return false;
		} else if (!currentVolume.equals(other.currentVolume))
			return false;
		if (metaPage == null) {
			if (other.metaPage != null)
				return false;
		} else if (!metaPage.equals(other.metaPage))
			return false;
		if (metaVolume == null) {
			if (other.metaVolume != null)
				return false;
		} else if (!metaVolume.equals(other.metaVolume))
			return false;
		if (volumeCount == null) {
			if (other.volumeCount != null)
				return false;
		} else if (!volumeCount.equals(other.volumeCount))
			return false;
		return true;
	}

}