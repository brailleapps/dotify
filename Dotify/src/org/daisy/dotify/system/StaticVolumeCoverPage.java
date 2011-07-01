package org.daisy.dotify.system;

import java.util.List;

import org.daisy.dotify.formatter.dom.Row;

/**
 * @author Joel Håkansson
 * @deprecated replaced by the volume-template feature in OBFL
 */
public class StaticVolumeCoverPage implements VolumeCoverPage {
	private final List<Row> rows;
	
	public StaticVolumeCoverPage(List<Row> rows) {
		this.rows = rows;
	}

	public List<Row> buildPage(int volumeNo, int volumeCount) {
		return rows;
	}

}
