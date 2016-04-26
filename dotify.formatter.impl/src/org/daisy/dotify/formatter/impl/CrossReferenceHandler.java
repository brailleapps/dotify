package org.daisy.dotify.formatter.impl;


class CrossReferenceHandler implements CrossReferences {
	private final LookupHandler<String, Integer> pageRefs;
	private final LookupHandler<String, Integer> volumeRefs;
	private final LookupHandler<Integer, Iterable<AnchorData>> anchorRefs;
	private final VolumeSplitter splitter;
	
	CrossReferenceHandler(VolumeSplitter splitter) {
		this.pageRefs = new LookupHandler<>();
		this.volumeRefs = new LookupHandler<>();
		this.anchorRefs = new LookupHandler<>();
		this.splitter = splitter;
	}
	
	public Integer getVolumeNumber(String refid) {
		return volumeRefs.get(refid);
	}
	
	void setVolumeNumber(String refid, int volume) {
		volumeRefs.put(refid, volume);
	}
	
	public Integer getPageNumber(String refid) {
		return pageRefs.get(refid);
	}
	
	void setPageNumber(String refid, int page) {
		pageRefs.put(refid, page);
	}
	
	public Iterable<AnchorData> getAnchorData(int volume) {
		return anchorRefs.get(volume);
	}
	
	void setAnchorData(int volume, Iterable<AnchorData> data) {
		anchorRefs.put(volume, data);
	}

	boolean isDirty() {
		return pageRefs.isDirty() || volumeRefs.isDirty() || anchorRefs.isDirty();
	}
	
	void setDirty(boolean value) {
		pageRefs.setDirty(value);
		volumeRefs.setDirty(value);
		anchorRefs.setDirty(value);
	}

	@Override
	public int getVolumeCount() {
		//FIXME: temporary
		return splitter.getVolumeCount();
	}
}
