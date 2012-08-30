package org.daisy.dotify.formatter.dom;



public class TextSegment implements Segment {
	private final CharSequence chars;
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
