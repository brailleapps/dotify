package org.daisy.dotify.formatter;

public class PageNumberReference implements BlockContents {
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

}
