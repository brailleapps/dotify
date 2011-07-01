package org.daisy.dotify.formatter.core;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.Stack;

import org.daisy.dotify.formatter.dom.BlockEvent;
import org.daisy.dotify.formatter.dom.EventContents;
import org.daisy.dotify.formatter.dom.TocEvent;
import org.daisy.dotify.formatter.dom.EventContents.ContentType;


/**
 * Provides table of contents entries to be used when building a Table of Contents
 * @author Joel Håkansson
 */
public class TableOfContents implements Iterable<BlockEvent> {
	private final Stack<BlockEvent> data;
	private final LinkedHashMap<String, String> refs;
	
	public TableOfContents() {
		this.data = new Stack<BlockEvent>();
		this.refs = new LinkedHashMap<String, String>();
		
	}
	
	private void collectIds(BlockEvent e) {
		String tocId = ((TocEvent)e).getTocId();
		if (tocId!=null) {
			if (refs.put(tocId, ((TocEvent)e).getRefId())!=null) {
				throw new RuntimeException("Identifier is not unique: " + tocId);
			}
		}
		for (EventContents c : e) {
			switch (c.getContentType()) {
			case TOC_ENTRY:
				collectIds((TocEvent)c);
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

	/**
	 * 
	 */
	private static final long serialVersionUID = 4646831324973203983L;

	public Iterator<BlockEvent> iterator() {
		return data.iterator();
	}

}
