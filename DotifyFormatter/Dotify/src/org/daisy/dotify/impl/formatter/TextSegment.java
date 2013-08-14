package org.daisy.dotify.impl.formatter;

import org.daisy.dotify.formatter.BlockProperties;
import org.daisy.dotify.formatter.TextProperties;



class TextSegment implements Segment {
	private CharSequence chars;
	private final TextProperties tp;
	private final BlockProperties p;
	
	public TextSegment(CharSequence chars, TextProperties tp, BlockProperties p) {
		this.chars = chars;
		this.tp = tp;
		this.p = p;
	}
	
	public CharSequence getChars() {
		return chars;
	}

	public void setChars(CharSequence chars) {
		this.chars = chars;
	}

	public TextProperties getTextProperties() {
		return tp;
	}

	public SegmentType getSegmentType() {
		return SegmentType.Text;
	}

	public BlockProperties getBlockProperties() {
		return p;
	}
	
}
