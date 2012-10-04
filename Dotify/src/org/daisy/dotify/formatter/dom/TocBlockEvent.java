package org.daisy.dotify.formatter.dom;

/**
 * Provides an interface for TOC block event.
 * 
 * @author Joel Håkansson
 */
public class TocBlockEvent extends BlockEventImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1378970818712629309L;
	private final String refId;
	private final String tocId;
	
	public TocBlockEvent(String refId, String tocId, BlockProperties props) {
		super(props);
		this.refId = refId;
		this.tocId = tocId;
	}

	public ContentType getContentType() {
		return ContentType.TOC_ENTRY;
	}
	
	public String getRefId() {
		return refId;
	}
	
	public String getTocId() {
		return tocId;
	}

}