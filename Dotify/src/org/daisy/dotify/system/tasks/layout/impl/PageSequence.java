package org.daisy.dotify.system.tasks.layout.impl;

import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;

public interface PageSequence extends Iterable<Page> {
	public LayoutMaster getLayoutMaster();
	public int getSize();
	public Page getPage(int index);
	public int getOffset();
}
