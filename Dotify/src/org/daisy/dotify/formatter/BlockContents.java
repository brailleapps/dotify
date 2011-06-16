package org.daisy.dotify.formatter;

public interface BlockContents {
	public enum ContentType {PCDATA, LEADER, MARKER, ANCHOR, BR, EVALUATE, BLOCK, PAGE_NUMBER};  
	public ContentType getContentType();
}
