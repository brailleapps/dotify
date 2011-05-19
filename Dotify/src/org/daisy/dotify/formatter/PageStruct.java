package org.daisy.dotify.formatter;

import org.daisy.dotify.text.StringFilter;


public interface PageStruct extends Iterable<PageSequence> {
	public StringFilter getFilter();
}