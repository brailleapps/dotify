package org.daisy.dotify.system.tasks.cover;

import java.util.List;

import org.daisy.dotify.system.tasks.layout.flow.Row;


public class StaticVolumeCoverPage implements VolumeCoverPage {
	private final List<Row> rows;
	
	public StaticVolumeCoverPage(List<Row> rows) {
		this.rows = rows;
	}

	public List<Row> buildPage(int volumeNo, int volumeCount) {
		return rows;
	}

}
