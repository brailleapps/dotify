package org.daisy.dotify.system;

import java.util.List;

import org.daisy.dotify.formatter.dom.Row;

/**
 * 
 * @author Joel Håkansson
 * @deprecated replaced by the volume-template feature in OBFL
 */
public interface VolumeCoverPage {
	
	/**
	 * Build cover page for this volume
	 * @param volumeNo
	 * @param volumeCount
	 * @return returns the page as an ArrayList of Rows
	 */
	public List<Row> buildPage(int volumeNo, int volumeCount);

}
