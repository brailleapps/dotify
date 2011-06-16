package org.daisy.dotify.formatter;

public interface VolumeSplitter {
	
	public SplitterData calculate(BookStruct book);

	public void setTargetVolumeSize(int i);
}
