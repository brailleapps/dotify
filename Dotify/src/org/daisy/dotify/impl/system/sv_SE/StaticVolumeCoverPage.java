package org.daisy.dotify.impl.system.sv_SE;

import java.util.List;

import org.daisy.dotify.book.Row;

/**
 * @author Joel Håkansson
 * @deprecated replaced by the volume-template feature in OBFL
 */
class StaticVolumeCoverPage implements VolumeCoverPage {
	private final List<Row> rows;
	
	StaticVolumeCoverPage(List<Row> rows) {
		this.rows = rows;
	}

	public List<Row> buildPage(int volumeNo, int volumeCount, int pageHeight) {
		return rows;
	}

}
