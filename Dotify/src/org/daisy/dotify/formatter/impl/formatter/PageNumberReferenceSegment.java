package org.daisy.dotify.formatter.impl.formatter;

import org.daisy.dotify.formatter.dom.PageNumberReference;
import org.daisy.dotify.formatter.obfl.NumeralField.NumeralStyle;

class PageNumberReferenceSegment extends PageNumberReference implements Segment {

	public PageNumberReferenceSegment(String refid, NumeralStyle style) {
		super(refid, style);
	}

	public SegmentType getSegmentType() {
		return SegmentType.Reference;
	}

}
