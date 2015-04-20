package org.daisy.dotify.formatter.impl;

class MarginComponent {
	private final String border;
	private final int offset;

	MarginComponent(String border, int offset) {
		this.border = border;
		this.offset = offset;
	}

	String getBorder() {
		return border;
	}

	int getOffset() {
		return offset;
	}

}
