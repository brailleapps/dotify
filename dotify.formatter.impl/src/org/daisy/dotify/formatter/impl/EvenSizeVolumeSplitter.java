package org.daisy.dotify.formatter.impl;

import java.util.logging.Logger;

class EvenSizeVolumeSplitter implements VolumeSplitter {
	private final static Logger logger = Logger.getLogger(EvenSizeVolumeSplitter.class.getCanonicalName());
	private EvenSizeVolumeSplitterCalculator sdc;
	int splitterMax = 0;
	int volumeOffset = 0;
	int volsMin = Integer.MAX_VALUE;
	private final VariablesHandler vh;

	EvenSizeVolumeSplitter(VariablesHandler vh) {
		this.vh = vh;
	}
	
	@Override
	public void setSplitterMax(int splitterMax) {
		this.splitterMax = splitterMax;
	}
	
	@Override
	public int getSplitterMax() {
		return splitterMax;
	}
	
	@Override
	public void updateSheetCount(int sheets) {
		EvenSizeVolumeSplitterCalculator esc = new EvenSizeVolumeSplitterCalculator(sheets, splitterMax, volumeOffset);
		// this fixes a problem where the volume overhead pushes the
		// volume count up once the volume offset has been set
		if (volumeOffset == 1 && esc.getVolumeCount() > volsMin + 1) {
			volumeOffset = 0;
			esc = new EvenSizeVolumeSplitterCalculator(sheets, splitterMax, volumeOffset);
		}

		volsMin = Math.min(esc.getVolumeCount(), volsMin);
		
		sdc = esc;
		vh.setVolumeCount(sdc.getVolumeCount());
	}
	
	@Override
	public void adjustVolumeCount(int sheets) {
		if (volumeOffset < 1) {
			//First check to see if the page increase can will be handled automatically without increasing volume offset 
			//in the next iteration (by supplying up-to-date overhead values)
			EvenSizeVolumeSplitterCalculator esv = new EvenSizeVolumeSplitterCalculator(sheets, splitterMax, volumeOffset);
			if (esv.equals(sdc)) {
				volumeOffset++;
			}
		} else {
			logger.warning("Could not fit contents even when adding a new volume.");
		}
	}

	@Override
	public int sheetsInVolume(int volIndex) {
		return sdc.sheetsInVolume(volIndex);
	}
}
