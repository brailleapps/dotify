package org.daisy.dotify.formatter.dom;

/**
 * Provides an interface for TOC block event.
 * 
 * @author Joel Håkansson
 */
public interface TocBlockEvent extends BlockEvent {

	public String getRefId();
	
	public String getTocId();

}
