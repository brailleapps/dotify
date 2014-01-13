package org.daisy.dotify.formatter.impl;
class MarginProperties {
	private final String margin;
	private final boolean spaceOnly;
	
	MarginProperties() {
		this("", true);
	}
	
	MarginProperties(String margin, boolean spaceOnly) {
		this.margin = margin;
		this.spaceOnly = spaceOnly;
	}

	public String getContent() {
		return margin;
	}

	@Override
	public String toString() {
		return margin;
	}

	public boolean isSpaceOnly() {
		return spaceOnly;
	}

}