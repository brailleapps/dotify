package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.List;

import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.api.writer.Row;
import org.daisy.dotify.api.writer.SectionProperties;

/**
 * Provides a method for writing pages to a PagedMediaWriter,
 * adding headers and footers as required by the layout.
 * @author Joel Håkansson
 */
class WriterHandler {
	
	WriterHandler() {
	}
	/**
	 * Writes this structure to the suppled PagedMediaWriter.
	 * @param writer the PagedMediaWriter to write to
	 * @throws IOException if IO fails
	 */
	void write(Iterable<Volume> volumes, PagedMediaWriter writer) {
		for (Volume v : volumes) {
			boolean firstInVolume = true;
			for (PageSequence s : v.getContents()) {
				SectionPropertiesAdapter lm = new SectionPropertiesAdapter(s.getLayoutMaster());
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
			writer.newRow(r);
		}
	}
	
	private static class SectionPropertiesAdapter implements SectionProperties {
		private final LayoutMaster lm;
		
		public SectionPropertiesAdapter(LayoutMaster lm) {
			this.lm = lm;
		}

		@Override
		public int getPageWidth() {
			return lm.getPageWidth();
		}

		@Override
		public int getPageHeight() {
			return lm.getPageHeight();
		}

		@Override
		public float getRowSpacing() {
			return lm.getRowSpacing();
		}

		@Override
		public boolean duplex() {
			return lm.duplex();
		}
		
	}

}