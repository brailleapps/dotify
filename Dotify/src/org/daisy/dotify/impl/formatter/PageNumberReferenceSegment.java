package org.daisy.dotify.impl.formatter;

import org.daisy.dotify.formatter.obfl.NumeralField.NumeralStyle;

class PageNumberReferenceSegment implements Segment {
	private final String refid;
	private final NumeralStyle style;
	
	public PageNumberReferenceSegment(String refid, NumeralStyle style) {
		this.refid = refid;
		this.style = style;
	}

	/**
	 * Gets the identifier to the reference location.
	 * @return returns the reference identifier
	 */
	public String getRefId() {
		return refid;
	}
	
	/**
	 * Gets the numeral style for this page number reference
	 * @return returns the numeral style
	 */
	public NumeralStyle getNumeralStyle() {
		return style;
	}

	public boolean canContainEventObjects() {
		return false;
	}

	public SegmentType getSegmentType() {
		return SegmentType.Reference;
	}

}
