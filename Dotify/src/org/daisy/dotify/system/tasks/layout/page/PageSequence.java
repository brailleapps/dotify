package org.daisy.dotify.system.tasks.layout.page;


public interface PageSequence extends Iterable<Page> {
	public LayoutMaster getLayoutMaster();
	public int getSize();
	public Page getPage(int index);
	public int getOffset();
}
