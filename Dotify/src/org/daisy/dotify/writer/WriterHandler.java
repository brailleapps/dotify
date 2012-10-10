package org.daisy.dotify.writer;

import java.io.IOException;
import java.util.List;

import org.daisy.dotify.book.Page;
import org.daisy.dotify.book.PageSequence;
import org.daisy.dotify.book.Row;
import org.daisy.dotify.formatter.FormatterException;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.tools.StringTools;

/**
 * Provides a method for writing pages to a PagedMediaWriter,
 * adding headers and footers as required by the layout.
 * @author Joel Håkansson
 */
public class WriterHandler {
	private final String marginCharacter;
	
	public WriterHandler(String marginCharacter) {
		this.marginCharacter = marginCharacter;
	}
	/**
	 * Writes this structure to the suppled PagedMediaWriter.
	 * @param writer the PagedMediaWriter to write to
	 * @throws IOException if IO fails
	 */
	public void write(Iterable<Volume> volumes, PagedMediaWriter writer) throws IOException {
		try {
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
						writePage(writer, p, lm);
					}
				}
			}
		} catch (FormatterException e) {
			IOException ex = new IOException("Layout exception");
			ex.initCause(e);
			throw ex;
		}
	}
	
	private void writePage(PagedMediaWriter writer, Page p, LayoutMaster lm) throws FormatterException {
		writer.newPage();
		int pagenum = p.getPageIndex()+1;
		List<Row> rows = p.getRows();
		for (Row row : rows) {
			if (row.getChars().length()>0) {
				int margin = ((pagenum % 2 == 0) ? lm.getOuterMargin() : lm.getInnerMargin()) + row.getLeftMargin();
				// remove trailing whitespace
				String chars = row.getChars().replaceAll("\\s*\\z", "");
				// add left margin
				int rowWidth = StringTools.length(chars)+row.getLeftMargin();
				String r = 	StringTools.fill(marginCharacter, margin) + chars;
				if (rowWidth>lm.getFlowWidth()) {
					throw new FormatterException("Row is too long (" + rowWidth + "/" + lm.getFlowWidth() + ") '" + chars + "'");
				}
				writer.newRow(r);
			} else {
				writer.newRow();
			}
		}
	}

}
