package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockPosition;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.formatter.impl.Segment.SegmentType;


class BlockImpl implements Block {
	private String blockId;
	private int spaceBefore;
	private int spaceAfter;
	private FormattingTypes.BreakBefore breakBefore;
	private FormattingTypes.Keep keep;
	private int keepWithNext;
	private int keepWithPreviousSheets;
	private int keepWithNextSheets;
	private String id;
	private Stack<Segment> segments;
	private final RowDataProperties.Builder rdp;
	private BlockContentManager rdm;
	private BlockPosition verticalPosition;
	private SingleLineDecoration leadingDecoration = null;
	private SingleLineDecoration trailingDecoration = null;

	
	BlockImpl(String blockId, RowDataProperties.Builder rdp) {
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		this.breakBefore = FormattingTypes.BreakBefore.AUTO;
		this.keep = FormattingTypes.Keep.AUTO;
		this.keepWithNext = 0;
		this.keepWithPreviousSheets = 0;
		this.keepWithNextSheets = 0;
		this.id = "";
		this.blockId = blockId;
		this.segments = new Stack<Segment>();
		this.rdp = rdp;
		this.rdm = null;
		this.verticalPosition = null;
	}

	public void addMarker(Marker m) {
		segments.add(new MarkerSegment(m));
	}
	
	public void addAnchor(String ref) {
		segments.add(new AnchorSegment(ref));
	}
	
	public void newLine(MarginProperties leftIndent) {
		segments.push(new NewLineSegment(leftIndent));
	}
	
	public void addChars(CharSequence c, TextProperties tp) {
		if (segments.size() > 0 && segments.peek().getSegmentType() == SegmentType.Text) {
			TextSegment ts = ((TextSegment) segments.peek());
			if (ts.getTextProperties().equals(tp)) {
				// Logger.getLogger(this.getClass().getCanonicalName()).finer("Appending chars to existing text segment.");
				ts.setText(ts.getText() + "" + c);
				return;
			}
		}
		segments.push(new TextSegment(c.toString(), tp));
	}
	
	public void insertLeader(Leader l) {
		segments.push(new LeaderSegment(l));
	}
	
	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		segments.push(new PageNumberReferenceSegment(identifier, numeralStyle));
	}
	
	public void setListItem(String label, FormattingTypes.ListStyle type) {
		rdp.listProperties(new ListItem(label, type));
	}

	public int getSpaceBefore() {
		return spaceBefore;
	}
	
	public int getSpaceAfter() {
		return spaceAfter;
	}
	
	public FormattingTypes.BreakBefore getBreakBeforeType() {
		return breakBefore;
	}
	
	public FormattingTypes.Keep getKeepType() {
		return keep;
	}
	
	public int getKeepWithNext() {
		return keepWithNext;
	}
	
	public int getKeepWithPreviousSheets() {
		return keepWithPreviousSheets;
	}
	
	public int getKeepWithNextSheets() {
		return keepWithNextSheets;
	}
	
	public String getIdentifier() {
		return id;
	}

	public BlockPosition getVerticalPosition() {
		return verticalPosition;
	}
	
	public void addSpaceBefore(int spaceBefore) {
		this.spaceBefore += spaceBefore;
	}
	
	public void addSpaceAfter(int spaceAfter) {
		this.spaceAfter += spaceAfter;
	}
	
	public void setBreakBeforeType(FormattingTypes.BreakBefore breakBefore) {
		this.breakBefore = breakBefore;
	}
	
	public void setKeepType(FormattingTypes.Keep keep) {
		this.keep = keep;
	}
	
	public void setKeepWithNext(int keepWithNext) {
		this.keepWithNext = keepWithNext;
	}
	
	public void setKeepWithPreviousSheets(int keepWithPreviousSheets) {
		this.keepWithPreviousSheets = keepWithPreviousSheets;
	}
	
	public void setKeepWithNextSheets(int keepWithNextSheets) {
		this.keepWithNextSheets = keepWithNextSheets;
	}
	
	public void setIdentifier(String id) {
		this.id = id;
	}

	public void setVerticalPosition(BlockPosition vertical) {
		this.verticalPosition = vertical;
	}

	public String getBlockIdentifier() {
		return blockId;
	}
	
	public BlockContentManager getBlockContentManager(CrossReferences refs) {
		if (rdm==null || rdm.isVolatile()) {
			rdm = new BlockContentManagerImpl(segments, rdp.build(), refs);
		}
		return rdm;
	}
	
	public SingleLineDecoration getLeadingDecoration() {
		return leadingDecoration;
	}
	
	public void setLeadingDecoration(SingleLineDecoration value) {
		this.leadingDecoration = value;
	}
	
	public SingleLineDecoration getTrailingDecoration() {
		return trailingDecoration;
	}

	public void setTrailingDecoration(SingleLineDecoration value) {
		this.trailingDecoration = value;
	}
	
	public RowDataProperties getRowDataProperties() {
		return rdp.build();
	}

}
