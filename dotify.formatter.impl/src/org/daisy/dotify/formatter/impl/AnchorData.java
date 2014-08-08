package org.daisy.dotify.formatter.impl;

import java.util.List;

class AnchorData {
	private final int pageIndex;
	private final List<String> refs;
	
	AnchorData(int pageIndex, List<String> refs) {
		this.pageIndex = pageIndex;
		this.refs = refs;
	}

	int getPageIndex() {
		return pageIndex;
	}
	
	List<String> getAnchors() {
		return refs;
	}
}
