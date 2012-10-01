package org.daisy.dotify.formatter.dom;


/**
 * Provides a complete braille book.
 *  
 * @author Joel Håkansson
 */
public interface BookStruct {
	
	public VolumeStruct getVolumeStruct();

	/**
	 * Gets static or generated content to be placed first in a new volume
	 * @param volumeNumber the volume number to get content for, one-based
	 * @return returns the content
	 */
	//public PageStruct getPreVolumeContents(int volumeNumber);
	
	/**
	 * Gets static or generated content to be placed last in a volume
	 * @param volumeNumber the volume number to get content for, one-based
	 * @return returns the content
	 */
	//public PageStruct getPostVolumeContents(int volumeNumber);
	
	/**
	 * Gets the maximum number of sheets allowed in the specified volume.
	 * @param volumeNumber the volume number to get the maximum size for, one based.
	 * @return returns the number of sheets allowed
	 */
	//public int getVolumeMaxSize(int volumeNumber);

	/**
	 * Gets the contents of the book
	 * @return returns the contents of the book
	 */
	//public PageStruct getContentsPageStruct();
	
	//public void setVolumeStruct(VolumeStruct volumeStruct);
	
	/**
	 * Returns true if any requested id was not found since it was last reset
	 * or if an id has changed page or volume between requests.
	 * @return returns true if, and only if, at least one request to getPage or getVolumeNumber
	 * returned null or a different location than before, false otherwise
	 */
	//public boolean isDirty();
	
	/**
	 * Attempts to reset dirty to false. This may involve re-formatting the contents.
	 */
	//public void resetDirty();

}
