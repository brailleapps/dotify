package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.BookStruct;
import org.daisy.dotify.formatter.VolumeSplitter;
import org.daisy.dotify.formatter.VolumeStruct;

public class SimpleVolumeSplitter implements VolumeSplitter {
	private int splitterMax = 49;
	
	public SimpleVolumeSplitter() { }

	public VolumeStruct calculate(BookStruct book) {
		return new VolumeStructImpl(book, splitterMax);
	}

	public void setTargetVolumeSize(int i) {
		this.splitterMax = i;
	}

}
