package org.daisy.dotify.book;

import org.daisy.dotify.paginator.PageStruct;


class VolData {
	private PageStruct preVolData;
	private PageStruct postVolData;
	private int preVolSize;
	private int postVolSize;
	private int targetVolSize;
	
	public VolData() {
		this.preVolSize = 0;
		this.postVolSize = 0;
		this.targetVolSize = 0;
	}

	public PageStruct getPreVolData() {
		return preVolData;
	}

	public void setPreVolData(PageStruct preVolData) {
		preVolSize = PageTools.countSheets(preVolData.getContents());
		this.preVolData = preVolData;
	}

	public PageStruct getPostVolData() {
		return postVolData;
	}

	public void setPostVolData(PageStruct postVolData) {
		postVolSize = PageTools.countSheets(postVolData.getContents());
		this.postVolData = postVolData;
	}

	public int getPreVolSize() {
		return preVolSize;
	}

	public int getPostVolSize() {
		return postVolSize;
	}
	
	public int getVolOverhead() {
		return preVolSize + postVolSize;
	}

	public int getTargetVolSize() {
		return targetVolSize;
	}

	public void setTargetVolSize(int targetVolSize) {
		this.targetVolSize = targetVolSize;
	}
	

}
