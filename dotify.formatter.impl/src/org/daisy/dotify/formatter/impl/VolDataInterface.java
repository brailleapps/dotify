package org.daisy.dotify.formatter.impl;


interface VolDataInterface {

	public PageStruct getPreVolData();
	public PageStruct getPostVolData();
	public int getPreVolSize();
	public int getPostVolSize();
	public int getVolOverhead();
	public int getTargetVolSize();
	
}
