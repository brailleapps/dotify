package org.daisy.dotify.formatter.impl;

/**
 * Provides information needed to split a book into volumes. 
 * 
 * @author Joel Håkansson
 */
public class EvenSizeVolumeSplitterCalculator {
	private final int sheets;
	// breakpoint, in sheets
	private final int breakpoint;
	// number of volumes with breakpoint sheets
	private final int sheetsPerVolumeBreakpoint;
	
	private final int volsWithBpSheets;
	// number of volumes
	private final int volumes;

	/**
	 * @param sheets, total number of sheets
	 * @param splitterMax, maximum number of sheets in a volume
	 */
	public EvenSizeVolumeSplitterCalculator(int sheets, int splitterMax) {
		volumes = (int)Math.ceil(sheets/(double)splitterMax);
		this.sheets = sheets;
		this.breakpoint = (int)Math.ceil(sheets/(double)volumes);
		int slv = sheets - (breakpoint * (volumes - 1));
		this.volsWithBpSheets = volumes - (breakpoint - slv);
		this.sheetsPerVolumeBreakpoint = breakpoint*volsWithBpSheets;
	}
	
	/**
	 * Tests if the supplied sheetIndex is a breakpoint. This sheetIndex counts all sheets,
	 * including sheets inserted in volume splitting. 
	 * @param sheetIndex sheet index, one based 
	 * @return returns true if the sheet is a breakpoint, false otherwise
	 * @throws IndexOutOfBoundsException if sheetIndex is outside of agreed boundaries
	 */
	public boolean isBreakpoint(int sheetIndex) {
		if (sheetIndex<1) {
			throw new IndexOutOfBoundsException("Sheet index must be greater than zero: " + sheetIndex);
		}
		if (sheetIndex>sheets) {
			throw new IndexOutOfBoundsException("Sheet index must not exceed agreed value.");
		}
		if (sheetIndex<sheetsPerVolumeBreakpoint) {
			// the number of volumes with breakpoint sheets has not passed 
			return (sheetIndex % breakpoint) == 0;
		} else {
			// offset the index with the full volumes and use breakpoint - 1 for the rest
			return ((sheetIndex - sheetsPerVolumeBreakpoint) % (breakpoint - 1)) == 0;
		}
	}
	
	/**
	 * 
	 * @param volIndex, volume index, one-based
	 * @return
	 */
	public int sheetsInVolume(int volIndex) {
		if (volIndex<=volsWithBpSheets) {
			return breakpoint;
		} else {
			return breakpoint-1;
		}
	}
	
	public int getSheetCount() {
		return sheets;
	}

	/**
	 * Gets the number of volumes.
	 * @return returns the number of volumes
	 */
	public int getVolumeCount() {
		return volumes;
	}
	
	/**
	 * Gets the volume for the supplied sheetIndex. This sheetIndex counts all sheets,
	 * including sheets inserted in volume splitting. 
	 * @param sheetIndex sheet index, one based 
	 * @return returns the volume number, one based
	 * @throws IndexOutOfBoundsException if sheetIndex is outside of agreed boundaries
	 */
	public int getVolumeForSheet(int sheetIndex) {
		if (sheetIndex<1) {
			throw new IndexOutOfBoundsException("Sheet index must be greater than zero: " + sheetIndex);
		}
		if (sheetIndex>sheets) {
			throw new IndexOutOfBoundsException("Sheet index must not exceed agreed value.");
		}
		if (sheetIndex<sheetsPerVolumeBreakpoint) {
			// the number of volumes with breakpoint sheets has not passed 
			return 1+(sheetIndex / breakpoint);
		} else {
			// offset the index with the full volumes and use breakpoint - 1 for the rest
			return 1+((sheetsPerVolumeBreakpoint) / breakpoint)+((sheetIndex - sheetsPerVolumeBreakpoint) / (breakpoint - 1));
		}
	}
	
}
