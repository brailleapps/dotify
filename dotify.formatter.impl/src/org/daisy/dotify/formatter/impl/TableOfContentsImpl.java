package org.daisy.dotify.formatter.impl;

import java.util.LinkedHashMap;
import java.util.Set;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.TableOfContents;


/**
 * Provides table of contents entries to be used when building a Table of Contents
 * @author Joel Håkansson
 */
class TableOfContentsImpl extends FormatterCoreImpl implements TableOfContents  {
	/**
	 * 
	 */
	private static final long serialVersionUID = -2198713822437968076L;
	private final LinkedHashMap<String, String> refs;
	
	public TableOfContentsImpl() {
		this.refs = new LinkedHashMap<String, String>();
	}
	
	public boolean containsTocID(String id) {
		return refs.containsKey(id);
	}
	
	public Set<String> getTocIdList() {
		return refs.keySet();
	}
	
	public String getRefForID(String id) {
		return refs.get(id);
	}

	public void startEntry(String refId, BlockProperties props) {
		String tocId;
		do {
			tocId = ""+((int)Math.round(99999999*Math.random()));
		} while (containsTocID(tocId));
		if (refs.put(tocId, refId)!=null) {
			throw new RuntimeException("Identifier is not unique: " + tocId);
		}
		startBlock(props, tocId);
	}
	
	public void endEntry() {
		endBlock();
	}

}
