package org.daisy.dotify.formatter.obfl;

import java.util.Set;


interface TableOfContents extends Iterable<BlockEvent> {

	public Set<String> getTocIdList();
	
	public String getRefForID(String id);
}
