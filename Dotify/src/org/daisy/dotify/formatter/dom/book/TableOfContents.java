package org.daisy.dotify.formatter.dom.book;

import java.util.Set;

import org.daisy.dotify.formatter.obfl.BlockEvent;

public interface TableOfContents extends Iterable<BlockEvent> {

	public Set<String> getTocIdList();
	
	public String getRefForID(String id);
}
