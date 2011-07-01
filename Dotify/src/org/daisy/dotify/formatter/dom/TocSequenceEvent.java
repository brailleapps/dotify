package org.daisy.dotify.formatter.dom;


public interface TocSequenceEvent extends VolumeSequenceEvent {
	enum TocRange {DOCUMENT, VOLUME};
	
	public String getTocName();
	public TocRange getRange();
	
	/**
	 * Returns true if this toc sequence applies to the supplied context
	 * @param volume
	 * @param volumeCount
	 * @return
	 */
	public boolean appliesTo(int volume, int volumeCount);
	
	/**
	 * Gets the TOC events 
	 * @param volume
	 * @param volumeCount
	 * @return
	 */
	public TocEvents getTocEvents(int volume, int volumeCount);	
	
}
