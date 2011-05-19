package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.formatter.PageSequence;
import org.daisy.dotify.formatter.PageStruct;
import org.daisy.dotify.text.StringFilter;

public class PageStructImpl extends Stack<PageSequence> implements PageStruct {
	private final StringFilter filters;
	
	public PageStructImpl(StringFilter filters) {
		this.filters = filters;
	}
	
	public StringFilter getFilter() {
		return filters;
	}

	private static final long serialVersionUID = 2591429059130956153L;

}
