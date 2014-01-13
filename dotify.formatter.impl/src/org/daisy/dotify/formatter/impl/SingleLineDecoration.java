package org.daisy.dotify.formatter.impl;

class SingleLineDecoration {
	private final String leftCorner;
	private final String rightCorner;
	private final String linePattern;

	public SingleLineDecoration(String leftCorner,
			String linePattern, String rightCorner) {
		super();
		this.leftCorner = leftCorner;
		this.linePattern = linePattern;
		this.rightCorner = rightCorner;
	}

	public String getLeftCorner() {
		return leftCorner;
	}

	public String getRightCorner() {
		return rightCorner;
	}

	public String getLinePattern() {
		return linePattern;
	}
	
	
}
