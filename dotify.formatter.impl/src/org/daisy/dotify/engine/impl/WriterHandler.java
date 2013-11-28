package org.daisy.dotify.engine.impl;

import java.io.IOException;
import java.util.List;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Page;
import org.daisy.dotify.api.formatter.PageSequence;
import org.daisy.dotify.api.formatter.Row;
import org.daisy.dotify.api.formatter.Volume;
import org.daisy.dotify.api.writer.PagedMediaWriter;

/**
 * Provides a method for writing pages to a PagedMediaWriter,
 * adding headers and footers as required by the layout.
 * @author Joel Håkansson
 */
class WriterHandler {
	
	public WriterHandler() {
	}
	/**
	 * Writes this structure to the suppled PagedMediaWriter.
	 * @param writer the PagedMediaWriter to write to
	 * @throws IOException if IO fails
	 */
	public void write(Iterable<Volume> volumes, PagedMediaWriter writer) {
		for (Volume v : volumes) {
			boolean firstInVolume = true;
			for (PageSequence s : v.getContents()) {
				LayoutMaster lm = s.getLayoutMaster();
				if (firstInVolume) {
					firstInVolume = false;
					writer.newVolume(lm);
				}
				writer.newSection(lm);
				for (Page p : s.getPages()) {
					writePage(writer, p);
				}
			}
		}
	}

	private void writePage(PagedMediaWriter writer, Page p) {
		writer.newPage();
		List<Row> rows = p.getRows();
		for (Row r : rows) {
			if (r.getChars().length() > 0) {
				writer.newRow(r);
			} else {
				writer.newRow();
			}
		}
	}

}