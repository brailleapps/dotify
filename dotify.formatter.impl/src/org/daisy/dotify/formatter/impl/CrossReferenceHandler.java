package org.daisy.dotify.formatter.impl;


class CrossReferenceHandler {
	private final LookupHandler<String, Integer> pageRefs;
	private final LookupHandler<String, Integer> volumeRefs;
	
	CrossReferenceHandler() {
		this.pageRefs = new LookupHandler<String, Integer>();
		this.volumeRefs = new LookupHandler<String, Integer>();
	}
	
	Integer getVolumeNumber(String refid) {
		return volumeRefs.get(refid);
	}
	
	void setVolumeNumber(String refid, int volume) {
		volumeRefs.put(refid, volume);
	}
	
	Integer getPageNumber(String refid) {
		return pageRefs.get(refid);
	}
	
	void setPageNumber(String refid, int page) {
		pageRefs.put(refid, page);
	}
	
	boolean isDirty() {
		return pageRefs.isDirty() || volumeRefs.isDirty();
	}
	
	void setDirty(boolean value) {
		pageRefs.setDirty(value);
		volumeRefs.setDirty(value);
	}
}
