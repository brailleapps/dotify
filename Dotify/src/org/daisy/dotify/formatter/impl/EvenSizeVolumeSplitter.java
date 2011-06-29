package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.VolumeSplitter;
import org.daisy.dotify.formatter.dom.BookStruct;
import org.daisy.dotify.formatter.dom.VolumeStruct;

/**
 * Provides an even size volume splitter. This splitter
 * does not consider the structure of the contents.
 *  
 * The 
 * @author Joel Håkansson
 */
public class EvenSizeVolumeSplitter implements VolumeSplitter {
	private int splitterMax = 49;
	
	public EvenSizeVolumeSplitter() { }

	public VolumeStruct split(BookStruct book) {
		return new EvenSizeVolumeStructImpl(book, splitterMax);
	}

	public void setTargetVolumeSize(int i) {
		this.splitterMax = i;
	}

}
