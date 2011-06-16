package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.SplitterData;

class SimpleSplitterData implements SplitterData {
	// breakpoint, in sheets
	private final int breakpoint;
	// number of volumes with breakpoint sheets
	private final int sheetsPerVolumeBreakpoint;
	// number of volumes
	private final int volumes;

	/**
	 * @param sheets, total number of sheets
	 * @param splitterMax, maximum number of sheets in a volume
	 */
	public SimpleSplitterData(int sheets, int splitterMax) {
		volumes = (int)Math.ceil(sheets/(double)splitterMax);
		this.breakpoint = (int)Math.ceil(sheets/(double)volumes);
		int slv = sheets - (breakpoint * (volumes - 1));
		int volsWithBpSheets = volumes - (breakpoint - slv);
		this.sheetsPerVolumeBreakpoint = breakpoint*volsWithBpSheets;
	}
	
	public boolean isBreakpoint(int sheetIndex) {
		if (sheetIndex<1) {
			throw new IllegalArgumentException("Sheet index must be greater than zero: " + sheetIndex);
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
}