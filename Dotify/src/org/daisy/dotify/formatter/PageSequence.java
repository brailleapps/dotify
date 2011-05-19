package org.daisy.dotify.formatter;



public interface PageSequence extends Iterable<Page> {
	public LayoutMaster getLayoutMaster();
	public int getSize();
	public Page getPage(int index);
	public int getOffset();
}
