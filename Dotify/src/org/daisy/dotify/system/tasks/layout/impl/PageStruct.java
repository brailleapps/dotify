package org.daisy.dotify.system.tasks.layout.impl;

import org.daisy.dotify.system.tasks.layout.text.StringFilter;


public interface PageStruct extends Iterable<PageSequence> {
	public StringFilter getFilter();
}