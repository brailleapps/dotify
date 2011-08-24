package org.daisy.dotify.formatter.core;

import org.daisy.dotify.formatter.core.NumeralField.NumeralStyle;
import org.daisy.dotify.formatter.dom.EventContents;


public class PageNumberReference implements EventContents {
	private final String refid;
	private final NumeralStyle style;
	
	public PageNumberReference(String refid, NumeralStyle style) {
		this.refid = refid;
		this.style = style;
	}

	public ContentType getContentType() {
		return ContentType.PAGE_NUMBER;
	}
	
	public String getRefId() {
		return refid;
	}
	
	public NumeralStyle getNumeralStyle() {
		return style;
	}

	public boolean canContainEventObjects() {
		return false;
	}

}
