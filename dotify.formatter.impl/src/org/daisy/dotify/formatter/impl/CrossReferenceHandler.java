package org.daisy.dotify.formatter.impl;

import java.util.HashSet;
import java.util.Set;

class CrossReferenceHandler {
	private final LookupHandler<String, Integer> pageRefs;
	private final LookupHandler<String, Integer> volumeRefs;
	private final LookupHandler<Integer, Iterable<AnchorData>> anchorRefs;
	private final VariablesHandler variables;
	private Set<String> pageIds;
	
	CrossReferenceHandler() {
		this.pageRefs = new LookupHandler<>();
		this.volumeRefs = new LookupHandler<>();
		this.anchorRefs = new LookupHandler<>();
		this.variables = new VariablesHandler();
		this.pageIds = new HashSet<>();
	}
	
	/**
	 * Gets the volume for the specified identifier.
	 * @param refid the identifier to get the volume for
	 * @return returns the volume number, one-based
	 */
	public Integer getVolumeNumber(String refid) {
		return volumeRefs.get(refid);
	}
	
	void setVolumeNumber(String refid, int volume) {
		volumeRefs.put(refid, volume);
	}
	
	/**
	 * Gets the page number for the specified identifier.
	 * @param refid the identifier to get the page for
	 * @return returns the page number, one-based
	 */
	public Integer getPageNumber(String refid) {
		return pageRefs.get(refid);
	}
	
	void setPageNumber(String refid, int page) {
		pageRefs.put(refid, page);
		if (!pageIds.add(refid)) {
			throw new IllegalArgumentException("Identifier not unique: " + refid);
		}
	}
	
	public Iterable<AnchorData> getAnchorData(int volume) {
		return anchorRefs.get(volume);
	}
	
	void setAnchorData(int volume, Iterable<AnchorData> data) {
		anchorRefs.put(volume, data);
	}
	
	public VariablesHandler getVariables() {
		return variables;
	}

	boolean isDirty() {
		return pageRefs.isDirty() || volumeRefs.isDirty() || anchorRefs.isDirty() || variables.isDirty();
	}
	
	void setDirty(boolean value) {
		pageRefs.setDirty(value);
		volumeRefs.setDirty(value);
		anchorRefs.setDirty(value);
		variables.setDirty(value);
	}
	
	void resetUniqueChecks() {
		pageIds = new HashSet<>();
	}

}
