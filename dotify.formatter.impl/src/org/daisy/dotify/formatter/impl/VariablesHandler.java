package org.daisy.dotify.formatter.impl;

/**
 * Handles variables that must remain constant from start to finish in volume breaking
 * for the process to be considered successful.
 * 
 * @author Joel Håkansson
 */
class VariablesHandler {
	private final LookupHandler<String, Integer> variables;
	private final static String VOLUMES_KEY = "volumes";
	private final static String SHEETS_IN_VOLUME = "sheets-in-volume-";
	private final static String SHEETS_IN_DOCUMENT = "sheets-in-document";
	private final static String PAGES_IN_VOLUME = "pages-in-volume-";
	private final static String PAGES_IN_DOCUMENT = "pages-in-document";
	
	VariablesHandler() {
		this.variables = new LookupHandler<>();
	}

	void setVolumeCount(int volumes) {
		variables.put(VOLUMES_KEY, volumes);
	}
	
	void setSheetsInVolume(int volume, int value) {
		variables.put(SHEETS_IN_VOLUME+volume, value);
	}
	
	void setSheetsInDocument(int value) {
		variables.put(SHEETS_IN_DOCUMENT, value);
	}
	
	void setPagesInVolume(int volume, int value) {
		variables.put(PAGES_IN_VOLUME+volume, value);
	}
	
	void setPagesInDocument(int value) {
		variables.put(PAGES_IN_DOCUMENT, value);
	}

	/**
	 * Gets the number of volumes.
	 * @return returns the number of volumes
	 */
	int getVolumeCount() {
		return variables.get(VOLUMES_KEY, 1);
	}
	
	int getSheetsInVolume(int volume) {
		return variables.get(SHEETS_IN_VOLUME+volume, 0);
	}

	int getSheetsInDocument() {
		return variables.get(SHEETS_IN_DOCUMENT, 0);
	}
	
	int getPagesInVolume(int volume) {
		return variables.get(PAGES_IN_VOLUME+volume, 0);
	}

	int getPagesInDocument() {
		return variables.get(PAGES_IN_DOCUMENT, 0);
	}
	
	boolean isDirty() {
		return variables.isDirty();
	}
	
	void setDirty(boolean value) {
		variables.setDirty(value);
	}

}
