package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.PageStruct;

interface VolDataInterface {

	public PageStruct getPreVolData();
	public PageStruct getPostVolData();
	public int getPreVolSize();
	public int getPostVolSize();
	public int getVolOverhead();
	public int getTargetVolSize();
	
}
