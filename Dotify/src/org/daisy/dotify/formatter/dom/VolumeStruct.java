package org.daisy.dotify.formatter.dom;

/**
 * Provides a container for a braille book
 * 
 * @author Joel Håkansson
 */
public interface VolumeStruct extends Iterable<Volume> {

	/**
	 * Gets the number of volumes.
	 * @return returns the number of volumes
	 */
	//public int getVolumeCount();
	
	/**
	 * Gets the volume for the supplied sheetIndex. This sheetIndex only counts content
	 * sheets, excluding sheets inserted in volume splitting. 
	 * @param sheetIndex sheet index, one based 
	 * @return returns the volume number, one based
	 * @throws IndexOutOfBoundsException if sheetIndex is outside of agreed boundaries
	 */
	//public int getVolumeForContentSheet(int sheetIndex);
}
