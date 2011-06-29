package org.daisy.dotify.formatter.dom;

/**
 * Provides a container for a physical volume of braille
 * @author Joel Håkansson
 */
public interface Volume {

	/**
	 * Gets static or generated contents to be placed first in a new volume
	 * @return returns the contents
	 */
	public Iterable<PageSequence> getPreVolumeContents();
	
	/**
	 * Gets static or generated contents to be placed last in a volume
	 * @return returns the contents
	 */
	public Iterable<PageSequence> getPostVolumeContents();
	
	/**
	 * Gets the contents
	 * @return returns the contents
	 */
	public Iterable<PageSequence> getBody();
}
