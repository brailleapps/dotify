package org.daisy.dotify.formatter.core;

import org.daisy.dotify.formatter.dom.EventContents;
import org.daisy.dotify.formatter.dom.EventContents.ContentType;


public class PageNumberReference implements EventContents {
	private final String refid;
	
	public PageNumberReference(String refid) {
		this.refid = refid;
	}

	public ContentType getContentType() {
		return ContentType.PAGE_NUMBER;
	}
	
	public String getRefId() {
		return refid;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
