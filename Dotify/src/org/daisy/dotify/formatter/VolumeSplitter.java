package org.daisy.dotify.formatter;

public interface VolumeSplitter {
	
	public VolumeStruct calculate(BookStruct book);

	public void setTargetVolumeSize(int i);
}
