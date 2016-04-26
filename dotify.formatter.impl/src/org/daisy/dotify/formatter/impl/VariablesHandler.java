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
	
	VariablesHandler() {
		this.variables = new LookupHandler<>();
	}

	void setVolumeCount(int volumes) {
		variables.put(VOLUMES_KEY, volumes);
	}

	/**
	 * Gets the number of volumes.
	 * @return returns the number of volumes
	 */
	int getVolumeCount() {
		return variables.get(VOLUMES_KEY, 1);
	}
	
	boolean isDirty() {
		return variables.isDirty();
	}
	
	void setDirty(boolean value) {
		variables.setDirty(value);
	}

}
