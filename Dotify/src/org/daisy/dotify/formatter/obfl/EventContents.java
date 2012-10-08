package org.daisy.dotify.formatter.obfl;


interface EventContents {
	public enum ContentType {PCDATA, LEADER, MARKER, ANCHOR, BR, EVALUATE, BLOCK, TOC_ENTRY, PAGE_NUMBER};  
	public ContentType getContentType();
	public boolean canContainEventObjects();

}
