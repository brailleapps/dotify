package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.FormattingTypes.Alignment;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.writer.Row;



/**
 * Row represents a single row of text
 * @author Joel Håkansson
 */
class RowImpl implements Row {
	private String chars;
	private List<Marker> markers;
	private List<String> anchors;
	private MarginProperties leftMargin;
	private MarginProperties rightMargin;
	private Alignment alignment;
	private Float rowSpacing;
	
	/**
	 * Create a new Row
	 * @param chars the characters on this row
	 */
	public RowImpl(String chars) {
		this(chars, new MarginProperties(), new MarginProperties());
	}
	public RowImpl(String chars, MarginProperties leftMargin, MarginProperties rightMargin) {
		this.chars = chars;
		this.markers = new ArrayList<Marker>();
		this.anchors = new ArrayList<String>();
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.alignment = Alignment.LEFT;
		this.rowSpacing = null;
	}

	/**
	 * Create a new empty Row
	 */
	public RowImpl() {
		this("");
	}

	/**
	 * Get the characters on this row
	 * @return returns the characters on the row
	 */
	public String getChars() {
		return chars;
	}

	public void setChars(String chars) {
		this.chars = chars;
	}
	/**
	 * Add a marker to the Row
	 * @param marker
	 */
	public void addMarker(Marker marker) {
		markers.add(marker);
	}

	/**
	 * Add an anchor to the Row
	 * @param ref
	 */
	public void addAnchor(String ref) {
		anchors.add(ref);
	}
	
	public void addAnchors(List<String> refs) {
		anchors.addAll(refs);
	}

	/**
	 * Add a collection of markers to the Row
	 * @param m
	 */
	public void addMarkers(List<Marker> m) {
		markers.addAll(m);
	}

	/**
	 * Get all markers on this Row
	 * @return returns the markers
	 */
	public List<Marker> getMarkers() {
		return markers;
	}
	
	/**
	 * Get all anchors on this Row
	 * @return returns an ArrayList of anchors
	 */
	public List<String> getAnchors() {
		return anchors;
	}

	/**
	 * Set the left margin
	 * @param value the left margin, in characters
	 */
	public void setLeftMargin(MarginProperties value) {
		leftMargin = value;
	}

	/**
	 * Get the left margin value for the Row, in characters
	 * @return returns the left margin
	 */
	public MarginProperties getLeftMargin() {
		return leftMargin;
	}

	public MarginProperties getRightMargin() {
		return rightMargin;
	}

	public void setRightMargin(MarginProperties rightMargin) {
		this.rightMargin = rightMargin;
	}

	/**
	 * Gets the alignment value for the row
	 * @return returns the alignment
	 */
	public Alignment getAlignment() {
		return alignment;
	}

	/**
	 * Sets the alignment value for the row
	 * @param alignment the new value
	 */
	public void setAlignment(Alignment alignment) {
		this.alignment = alignment;
	}

	public Float getRowSpacing() {
		return rowSpacing;
	}
	
	public void setRowSpacing(Float value) {
		this.rowSpacing = value;
	}

}