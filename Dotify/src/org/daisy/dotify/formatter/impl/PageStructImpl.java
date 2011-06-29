package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Stack;

import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.PageStruct;

public class PageStructImpl extends Stack<PageSequence> implements PageStruct {
	//private final StringFilter filters;
	HashMap<String, Integer> pageReferences;

	
	public PageStructImpl() {
		//this.filters = filters;
		this.pageReferences = new HashMap<String, Integer>();
	}
	
	/*public StringFilter getFilter() {
		return filters;
	}*/

	private static final long serialVersionUID = 2591429059130956153L;


	public Iterable<PageSequence> getContents() {
		return this;
	}

	public Integer getPageNumber(String refid) {
		return pageReferences.get(refid);
	}

}