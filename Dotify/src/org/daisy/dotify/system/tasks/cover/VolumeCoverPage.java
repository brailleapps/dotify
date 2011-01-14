package org.daisy.dotify.system.tasks.cover;

import java.util.List;

import org.daisy.dotify.system.tasks.layout.flow.Row;


public interface VolumeCoverPage {
	
	/**
	 * Build cover page for this volume
	 * @param volumeNo
	 * @param volumeCount
	 * @return returns the page as an ArrayList of Rows
	 */
	public List<Row> buildPage(int volumeNo, int volumeCount);

}
