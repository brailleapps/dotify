package org.daisy.dotify.formatter.impl;

public class SimpleSplitterData {
	final int sheets;
	// breakpoint, in sheets
	final int breakpoint;
	// number of volumes with breakpoint sheets
	final int sheetsPerVolumeBreakpoint;
	
	final int volsWithBpSheets;
	// number of volumes
	private final int volumes;

	/**
	 * @param sheets, total number of sheets
	 * @param splitterMax, maximum number of sheets in a volume
	 */
	public SimpleSplitterData(int sheets, int splitterMax) {
		volumes = (int)Math.ceil(sheets/(double)splitterMax);
		this.sheets = sheets;
		this.breakpoint = (int)Math.ceil(sheets/(double)volumes);
		int slv = sheets - (breakpoint * (volumes - 1));
		this.volsWithBpSheets = volumes - (breakpoint - slv);
		this.sheetsPerVolumeBreakpoint = breakpoint*volsWithBpSheets;
	}
	
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
	
	public int volumeCount() {
		return volumes;
	}
	
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
