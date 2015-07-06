package org.daisy.dotify.formatter.impl;

class MarginComponent {
	private final String border;
	private final int outer;
	private final int inner;

	MarginComponent(String border, int outerOffset, int innerOffset) {
		this.border = border;
		this.outer = outerOffset;
		this.inner = innerOffset;
	}

	String getBorder() {
		return border;
	}

	int getOuterOffset() {
		return outer;
	}
	
	int getInnerOffset() {
		return inner;
	}

}
