package org.daisy.dotify.system.tasks.layout.flow;

import java.io.Closeable;

import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;
import org.daisy.dotify.system.tasks.layout.page.Paginator;


/**
 * <p>Flow is the entry point when converting a hierarchical 
 * layout structure into pages.</p>
 * 
 * <p>An implementation of Flow interprets layout properties
 * and break blocks of characters into rows.</p>
 * 
 * <p>The rows are then passed on to the {@link Paginator}.</p>
 *  
 * @author Joel Håkansson, TPB
 *
 */
public interface Flow extends Closeable {
	
	/**
	 * Open the Flow with the supplied Paginator
	 * @param performer the Paginator that the result should
	 * be passed to
	 */
	public void open(Paginator performer);

	/**
	 * Start a new Sequence at the current position in the flow.
	 * @param props the SequenceProperties for the new sequence
	 */
	public void newSequence(SequenceProperties props);

	/**
	 * Start a new block with the supplied BlockProperties.
	 * @param props the BlockProperties of the new block
	 */
	public void startBlock(BlockProperties props);

	/**
	 * End the current block
	 */
	public void endBlock();
	
	/**
	 * Start a floating element
	 * @param id the identifier of the floating element
	 */
	public void startFloat(String id);
	
	/**
	 * End the floating element
	 */
	public void endFloat();

	/**
	 * Insert a marker at the current position in the flow
	 * @param marker the marker to insert
	 */
	public void insertMarker(Marker marker);
	
	/**
	 * Insert an anchor at the current position in the flow
	 * @param ref anchor name
	 */
	public void insertAnchor(String ref);
	
	/**
	 * Insert a leader at the current position in the flow
	 * @param leader the leader to insert
	 */
	public void insertLeader(Leader leader);

	/**
	 * Add chars to the current block
	 * @param chars the characters to add to the flow
	 */
	public void addChars(CharSequence chars);
	
	/**
	 * Add chars to the current block
	 * @param chars the characters to add to the flow
	 * @param props the SpanProperties for the characters 
	 */
	public void addChars(CharSequence chars, SpanProperties props);
	
	/**
	 * Explicitly break the current line, even if the line has space 
	 * left for more characters. The current block remains open.
	 */
	public void newLine();

	/**
	 * Add a LayoutMaster
	 * @param name The name of the LayoutMaster. This is the named used in when retrieving
	 * a master for a particular sequence from the {@link SequenceProperties}.
	 * @param master the LayoutMaster
	 */
	public void addLayoutMaster(String name, LayoutMaster master);

}
