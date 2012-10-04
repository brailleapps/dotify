package org.daisy.dotify.formatter.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Stack;

import org.daisy.dotify.formatter.dom.BlockEvent;
import org.daisy.dotify.formatter.dom.EventContents;
import org.daisy.dotify.formatter.dom.EventContents.ContentType;
import org.daisy.dotify.formatter.dom.book.TableOfContents;


/**
 * Provides table of contents entries to be used when building a Table of Contents
 * @author Joel Håkansson
 */
class TableOfContentsImpl implements TableOfContents {
	private final Stack<BlockEvent> data;
	private final LinkedHashMap<String, String> refs;
	
	public TableOfContentsImpl() {
		this.data = new Stack<BlockEvent>();
		this.refs = new LinkedHashMap<String, String>();
		
	}
	
	private void collectIds(BlockEvent e) {
		String tocId = ((TocBlockEvent)e).getTocId();
		if (tocId!=null) {
			if (refs.put(tocId, ((TocBlockEvent)e).getRefId())!=null) {
				throw new RuntimeException("Identifier is not unique: " + tocId);
			}
		}
		for (EventContents c : e) {
			switch (c.getContentType()) {
			case TOC_ENTRY:
				collectIds((TocBlockEvent)c);
				break;
			default:
				break;
			}
		}
	}
	
	public boolean add(BlockEvent e) {
		if (e.getContentType()!=ContentType.TOC_ENTRY) {
			throw new IllegalArgumentException("Can only add toc entries to a TOC");
		}
		collectIds(e);
		return data.add(e);
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
/*
	public LinkedHashMap<String, String> getIdIdRefMap() {
		return refs;
	}*/

	public Iterator<BlockEvent> iterator() {
		return data.iterator();
	}

}
