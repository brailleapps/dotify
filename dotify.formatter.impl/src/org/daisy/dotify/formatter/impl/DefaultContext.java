package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.Context;

class DefaultContext implements Context {
	private final Integer currentVolume, volumeCount, currentPage, metaVolume, metaPage;
	
	static class Builder {
		private Integer	currentVolume=null, 
						volumeCount=null,
						currentPage=null,
						metaVolume=null,
						metaPage=null; 
		
		Builder() {
		}
		
		private Builder(Context base) {
			this.currentVolume = base.getCurrentVolume();
			this.volumeCount = base.getVolumeCount();
			this.currentPage = base.getCurrentPage();
			this.metaVolume = base.getMetaVolume();
			this.metaPage = base.getMetaPage();
		}
		
		Builder currentVolume(Integer value) {
			this.currentVolume = value;
			return this;
		}
		
		Builder currentPage(Integer value) {
			this.currentPage = value;
			return this;
		}
		
		Builder volumeCount(Integer value) {
			this.volumeCount = value;
			return this;
		}
		
		Builder metaVolume(Integer value) {
			this.metaVolume = value;
			return this;
		}
		
		Builder metaPage(Integer value) {
			this.metaPage = value;
			return this;
		}
		
		DefaultContext build() {
			return new DefaultContext(this);
		}
	}
	
	public static DefaultContext.Builder from(Context base) {
		return new DefaultContext.Builder(base);
	}
	
	private DefaultContext(Builder builder) {
		this.currentVolume = builder.currentVolume;
		this.volumeCount = builder.volumeCount;
		this.currentPage = builder.currentPage;
		this.metaVolume = builder.metaVolume;
		this.metaPage = builder.metaPage;
	}
	
	public DefaultContext(Integer currentVolume, Integer volumeCount) {
		this.currentVolume = currentVolume;
		this.volumeCount = volumeCount;
		this.currentPage = null;
		this.metaVolume = null;
		this.metaPage = null;
	} 

	@Override
	public Integer getCurrentVolume() {
		return currentVolume;
	}

	@Override
	public Integer getVolumeCount() {
		return volumeCount;
	}

	@Override
	public Integer getCurrentPage() {
		return currentPage;
	}

	@Override
	public Integer getMetaVolume() {
		return metaVolume;
	}

	@Override
	public Integer getMetaPage() {
		return metaPage;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((currentPage == null) ? 0 : currentPage.hashCode());
		result = prime * result + ((currentVolume == null) ? 0 : currentVolume.hashCode());
		result = prime * result + ((metaPage == null) ? 0 : metaPage.hashCode());
		result = prime * result + ((metaVolume == null) ? 0 : metaVolume.hashCode());
		result = prime * result + ((volumeCount == null) ? 0 : volumeCount.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		DefaultContext other = (DefaultContext) obj;
		if (currentPage == null) {
			if (other.currentPage != null) {
				return false;
			}
		} else if (!currentPage.equals(other.currentPage)) {
			return false;
		}
		if (currentVolume == null) {
			if (other.currentVolume != null) {
				return false;
			}
		} else if (!currentVolume.equals(other.currentVolume)) {
			return false;
		}
		if (metaPage == null) {
			if (other.metaPage != null) {
				return false;
			}
		} else if (!metaPage.equals(other.metaPage)) {
			return false;
		}
		if (metaVolume == null) {
			if (other.metaVolume != null) {
				return false;
			}
		} else if (!metaVolume.equals(other.metaVolume)) {
			return false;
		}
		if (volumeCount == null) {
			if (other.volumeCount != null) {
				return false;
			}
		} else if (!volumeCount.equals(other.volumeCount)) {
			return false;
		}
		return true;
	}

}