package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.BookStruct;
import org.daisy.dotify.formatter.SplitterData;
import org.daisy.dotify.formatter.VolumeSplitter;
import org.daisy.dotify.formatter.utils.PageTools;

public class SimpleVolumeSplitter implements VolumeSplitter {
	private int splitterMax = 49;
	
	public SimpleVolumeSplitter() { }

	public SplitterData calculate(BookStruct struct) {
		int sheets = PageTools.countSheets(struct.getPageStruct().getContents());
		System.out.println("Content sheets: " + sheets);
		SimpleSplitterData sd = new SimpleSplitterData(sheets, splitterMax);
		for (int i=1;i<=sd.volumeCount();i++) {
			sheets += PageTools.countSheets(struct.getPreVolumeContents(i, sd.volumeCount()));
			sheets += PageTools.countSheets(struct.getPostVolumeContents(i, sd.volumeCount()));
		}
		//TODO: if pre and post pages use up the headroom in the splitter, recalculate once more
		System.out.println("Total sheets: " + sheets);
		return new SimpleSplitterData(sheets, splitterMax);
	}

	public void setTargetVolumeSize(int i) {
		this.splitterMax = i;
	}

}
