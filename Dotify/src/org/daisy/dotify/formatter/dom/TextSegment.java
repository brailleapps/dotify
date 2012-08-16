package org.daisy.dotify.formatter.dom;


public class TextSegment implements Segment {
	private final CharSequence chars;
	private final BlockProperties p;
	
	public TextSegment(CharSequence chars, BlockProperties p) {
		this.chars = chars;
		this.p = p;
	}
	
	public CharSequence getChars() {
		return chars;
	}

	public SegmentType getSegmentType() {
		return SegmentType.Text;
	}

	public BlockProperties getBlockProperties() {
		return p;
	}
	
}
