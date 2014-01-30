package org.daisy.dotify.formatter.impl;


interface EventContents {
	public enum ContentType {PCDATA, LEADER, MARKER, ANCHOR, BR, EVALUATE, BLOCK, STYLE, TOC_ENTRY, PAGE_NUMBER};  
	public ContentType getContentType();
	public boolean canContainEventObjects();

}
