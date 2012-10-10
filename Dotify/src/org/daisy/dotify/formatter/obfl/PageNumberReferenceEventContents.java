package org.daisy.dotify.formatter.obfl;

import org.daisy.dotify.formatter.obfl.NumeralField.NumeralStyle;

class PageNumberReferenceEventContents extends PageNumberReference
		implements EventContents {


	public PageNumberReferenceEventContents(String refid, NumeralStyle style) {
		super(refid, style);
	}

	public ContentType getContentType() {
		return ContentType.PAGE_NUMBER;
	}
}
