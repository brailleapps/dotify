package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Stack;

import org.daisy.dotify.formatter.dom.Block;
import org.daisy.dotify.formatter.dom.FormattingTypes;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.Row;

class BlockImpl extends Stack<Row> implements Block {
	private String blockId;
	private int spaceBefore;
	private int spaceAfter;
	private ArrayList<Marker> groupMarkers;
	private ArrayList<String> groupAnchors;
	private FormattingTypes.BreakBefore breakBefore;
	private FormattingTypes.Keep keep;
	private int keepWithNext;
	private String id;
	
	BlockImpl(String blockId) {
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		this.groupMarkers = new ArrayList<Marker>();
		this.groupAnchors = new ArrayList<String>();
		this.breakBefore = FormattingTypes.BreakBefore.AUTO;
		this.keep = FormattingTypes.Keep.AUTO;
		this.keepWithNext = 0;
		this.id = "";
		this.blockId = blockId;
	}
	
	public void addMarker(Marker m) {
		if (isEmpty()) {
			groupMarkers.add(m);
		} else {
			this.peek().addMarker(m);
		}
	}
	
	public void addAnchor(String ref) {
		if (isEmpty()) {
			groupAnchors.add(ref);
		} else {
			this.peek().addAnchor(ref);
		}
	}
	
	/**
	 * Get markers that are not attached to a row, i.e. markers that proceeds any text contents
	 * @return returns markers that proceeds this FlowGroups text contents
	 */
	public ArrayList<Marker> getGroupMarkers() {
		return groupMarkers;
	}
	
	public ArrayList<String> getGroupAnchors() {
		return groupAnchors;
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
	
	private static final long serialVersionUID = -3120988813614814721L;

	public int getRowCount() {
		return this.size();
	}

	public String getBlockIdentifier() {
		return blockId;
	}

}
