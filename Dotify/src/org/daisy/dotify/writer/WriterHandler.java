package org.daisy.dotify.writer;

import java.io.IOException;
import java.util.List;

import org.daisy.dotify.book.Volume;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.paginator.Page;
import org.daisy.dotify.paginator.PageSequence;

/**
 * Provides a method for writing pages to a PagedMediaWriter,
 * adding headers and footers as required by the layout.
 * @author Joel Håkansson
 */
public class WriterHandler {
	
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
		List<String> rows = p.getRows();
		for (String r : rows) {
			if (r.length() > 0) {
				writer.newRow(r);
			} else {
				writer.newRow();
			}
		}
	}

}