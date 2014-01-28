package org.daisy.dotify.obfl;

import org.daisy.dotify.api.formatter.NumeralStyle;

class PageNumberReferenceEventContents extends PageNumberReference
		implements EventContents {


	public PageNumberReferenceEventContents(String refid, NumeralStyle style) {
		super(refid, style);
	}

	public ContentType getContentType() {
		return ContentType.PAGE_NUMBER;
	}
}
