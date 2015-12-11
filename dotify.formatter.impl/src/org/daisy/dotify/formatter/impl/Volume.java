package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.common.collection.CompoundIterable;

/**
 * Provides a container for a physical volume of braille
 * @author Joel Håkansson
 */
class Volume {
	private CompoundIterable<PageSequence> ret;
	private Iterable<PageSequence> body;
	private PageStructBuilder preVolData;
	private PageStructBuilder postVolData;
	private final int volumeNumber;
	private int preVolSize;
	private int postVolSize;
	private int targetVolSize;
	
	Volume(int volumeNumber) {
		this.volumeNumber = volumeNumber;
		ret = null;
		this.preVolSize = 0;
		this.postVolSize = 0;
		this.targetVolSize = 0;
	}
	
	public PageStructBuilder getPreVolData() {
		return preVolData;
	}

	public Iterable<PageSequence> getBody() {
		return body;
	}

	public void setBody(Iterable<PageSequence> body) {
		ret = null;
		this.body = body;
	}

	public void setPreVolData(PageStructBuilder preVolData) {
		ret = null;
		//use the highest value to avoid oscillation
		preVolSize = Math.max(preVolSize, preVolData.countSheets());
		this.preVolData = preVolData;
	}

	public PageStructBuilder getPostVolData() {
		return postVolData;
	}

	public void setPostVolData(PageStructBuilder postVolData) {
		ret = null;
		//use the highest value to avoid oscillation
		postVolSize = Math.max(postVolSize, postVolData.countSheets());
		this.postVolData = postVolData;
	}
	
	public int getOverhead() {
		return preVolSize + postVolSize;
	}

	public int getTargetSize() {
		return targetVolSize;
	}

	public void setTargetVolSize(int targetVolSize) {
		this.targetVolSize = targetVolSize;
	}

	public int getVolumeNumber() {
		return volumeNumber;
	}

	/**
	 * Gets the contents
	 * @return returns the contents
	 */
	Iterable<PageSequence> getContents() {
		if (ret==null) {
			List<Iterable<PageSequence>> contents = new ArrayList<>();
			contents.add(preVolData);
			contents.add(body);
			contents.add(postVolData);
			ret = new CompoundIterable<>(contents);
		}
		return ret;
	}

}