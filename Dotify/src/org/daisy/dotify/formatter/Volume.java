package org.daisy.dotify.formatter;

public interface Volume {

	/**
	 * Gets static or generated content to be placed first in a new volume
	 * @return returns the content
	 */
	public Iterable<PageSequence> getPreVolumeContents();
	
	/**
	 * Gets static or generated content to be placed last in a volume
	 * @return returns the content
	 */
	public Iterable<PageSequence> getPostVolumeContents();
	
	public Iterable<PageSequence> getBody();
}
