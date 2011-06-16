package org.daisy.dotify.formatter;

public interface SplitterData {
	
	/**
	 * Tests if the supplied sheetIndex is a breakpoint
	 * @param sheetIndex sheet index, one based 
	 * @return returns true if the sheet is a breakpoint, false otherwise
	 * @throws IllegalArgumentException if sheetIndex is less than 1.
	 */
	public boolean isBreakpoint(int sheetIndex);
	
	public int volumeCount();
}