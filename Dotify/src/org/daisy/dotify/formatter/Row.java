package org.daisy.dotify.formatter;

import java.util.ArrayList;
import java.util.List;

/**
 * Row represents a single row of text
 * @author Joel Håkansson, TPB
 */
public class Row {
	private String chars;
	private List<Marker> markers;
	private List<String> anchors;
	private int leftMargin;
	/*
	private int spaceBefore;
	private int spaceAfter;
	*/
	
	/**
	 * Create a new Row
	 * @param chars the characters on this row
	 */
	public Row(String chars) {
		this.chars = chars;
		this.markers = new ArrayList<Marker>();
		this.anchors = new ArrayList<String>();
		this.leftMargin = 0;
		/*
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		*/
	}

	/**
	 * Create a new empty Row
	 */
	public Row() {
		this("");
	}

	/**
	 * Get the characters on this row
	 * @return returns the characters on the row
	 */
	public String getChars() {
		return chars;
	}
	
/*	public void appendChars(CharSequence c) {
		chars = chars.toString() + c;
	}*/

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
	public void setLeftMargin(int value) {
		leftMargin = value;
	}

	/**
	 * Get the left margin value for the Row, in characters
	 * @return returns the left margin
	 */
	public int getLeftMargin() {
		return leftMargin;
	}
/*
	public int getSpaceBefore() {
		return spaceBefore;
	}
	
	public int getSpaceAfter() {
		return spaceAfter;
	}
	
	public void setSpaceBefore(int value) {
		spaceBefore = value;
	}
	
	public void setSpaceAfter(int value) {
		spaceAfter = value;
	}
	*/

}