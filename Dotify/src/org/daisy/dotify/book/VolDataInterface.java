package org.daisy.dotify.book;

import org.daisy.dotify.paginator.PageStruct;

public interface VolDataInterface {

	public PageStruct getPreVolData();
	public PageStruct getPostVolData();
	public int getPreVolSize();
	public int getPostVolSize();
	public int getVolOverhead();
	public int getTargetVolSize();
	
}
