package org.daisy.dotify.obfl;


/**
 * Provides a TOC sequence event object.
 * 
 * @author Joel Håkansson
 */
interface TocSequenceEvent extends VolumeSequenceEvent {
	/**
	 * Defines TOC ranges.
	 */
	enum TocRange {
		/**
		 * Defines the TOC range to include the entire document
		 */
		DOCUMENT,
		/**
		 * Defines the TOC range to include entries within the volume
		 */
		VOLUME};

	public String getTocName();

	public TocRange getRange();
	
	/**
	 * Returns true if this toc sequence applies to the supplied context
	 * @param volume
	 * @param volumeCount
	 * @return returns true if this toc sequence applies to the supplied context, false otherwise
	 */
	public boolean appliesTo(int volume, int volumeCount);

	/**
	 * Gets the TOC events 
	 * @param volume
	 * @param volumeCount
	 * @return returns the TOC events
	 */
	public TocEvents getTocEvents(int volume, int volumeCount);

}