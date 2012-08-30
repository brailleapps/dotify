package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.formatter.core.NumeralField.NumeralStyle;
import org.daisy.dotify.formatter.core.PageNumberReference;
import org.daisy.dotify.formatter.dom.AnchorSegment;
import org.daisy.dotify.formatter.dom.Block;
import org.daisy.dotify.formatter.dom.BlockProperties;
import org.daisy.dotify.formatter.dom.CrossReferences;
import org.daisy.dotify.formatter.dom.FormattingTypes;
import org.daisy.dotify.formatter.dom.Leader;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.NewLineSegment;
import org.daisy.dotify.formatter.dom.RowDataManager;
import org.daisy.dotify.formatter.dom.Segment;
import org.daisy.dotify.formatter.dom.TextProperties;
import org.daisy.dotify.formatter.dom.TextSegment;


class BlockImpl implements Block {
	private String blockId;
	private int spaceBefore;
	private int spaceAfter;
	private FormattingTypes.BreakBefore breakBefore;
	private FormattingTypes.Keep keep;
	private int keepWithNext;
	private String id;
	private Stack<Segment> segments;
	private final RowDataProperties rdp;
	private RowDataManager rdm;

	
	BlockImpl(String blockId, RowDataProperties rdp) {
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		this.breakBefore = FormattingTypes.BreakBefore.AUTO;
		this.keep = FormattingTypes.Keep.AUTO;
		this.keepWithNext = 0;
		this.id = "";
		this.blockId = blockId;
		this.segments = new Stack<Segment>();
		this.rdp = rdp;
		this.rdm = null;
	}

	public void addMarker(Marker m) {
		segments.add(m);
	}
	
	public void addAnchor(String ref) {
		segments.add(new AnchorSegment(ref));
	}
	
	public void newLine(int leftIndent) {
		segments.push(new NewLineSegment(leftIndent));
	}
	
	public void addChars(CharSequence c, TextProperties tp, BlockProperties p) {
		segments.push(new TextSegment(c, tp, p));
	}
	
	public void insertLeader(Leader l) {
		segments.push(l);
	}
	
	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		segments.push(new PageNumberReference(identifier, numeralStyle));
	}
	
	public void setListItem(String label, FormattingTypes.ListStyle type) {
		rdp.setListItem(label, type);
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
	
	public String getIdentifier() {
		return id;
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
	
	public void setIdentifier(String id) {
		this.id = id;
	}

	public String getBlockIdentifier() {
		return blockId;
	}
	
	public RowDataManager getRowDataManager(CrossReferences refs) {
		if (rdm==null || rdm.isVolatile()) {
			rdm = new RowDataManagerImpl(segments, rdp, refs);
		}
		return rdm;
	}

}
