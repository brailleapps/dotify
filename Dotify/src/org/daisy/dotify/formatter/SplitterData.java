package org.daisy.dotify.formatter;

public interface SplitterData {
	
	/**
	 * Tests if the supplied sheetIndex is a breakpoint
	 * @param sheetIndex sheet index, one based 
	 * @return returns true if the sheet is a breakpoint, false otherwise
	 * @throws IndexOutOfBoundsException if sheetIndex is outside of agreed boundaries
	 */
	public boolean isBreakpoint(int sheetIndex);
	
	public int volumeCount();
	
	/**
	 * Gets the volume for the supplied sheetIndex
	 * @param sheetIndex
	 * @return returns the volume number, one based
	 * @throws IndexOutOfBoundsException if sheetIndex is outside of agreed boundaries
	 */
	public int getVolumeForSheet(int sheetIndex);
	
	public int getVolumeForContentSheet(int sheetIndex);
}