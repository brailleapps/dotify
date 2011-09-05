package org.daisy.dotify.formatter.dom;

/**
 * Provides a complete braille book.
 *  
 * @author Joel Håkansson
 */
public interface BookStruct {

	/**
	 * Gets static or generated content to be placed first in a new volume
	 * @param volumeNumber the volume number to get content for, one-based
	 * @return returns the content
	 */
	public Iterable<PageSequence> getPreVolumeContents(int volumeNumber, VolumeStruct volumeData);
	
	/**
	 * Gets static or generated content to be placed last in a volume
	 * @param volumeNumber the volume number to get content for, one-based
	 * @return returns the content
	 */
	public Iterable<PageSequence> getPostVolumeContents(int volumeNumber, VolumeStruct volumeData);

	/**
	 * Gets the contents of the book
	 * @return
	 */
	public PageStruct getPageStruct();

}
