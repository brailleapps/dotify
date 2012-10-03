package org.daisy.dotify.formatter;

import java.io.IOException;
import java.util.List;

import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.Row;
import org.daisy.dotify.formatter.dom.Volume;
import org.daisy.dotify.formatter.utils.LayoutTools;

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
				for (PageSequence s : v.getPreVolumeContents()) {
					LayoutMaster lm = s.getLayoutMaster();
					if (firstInVolume) {
						firstInVolume = false;
						writer.newVolume(lm);
					}
					writer.newSection(lm);
					for (Page p : s) {
						writePage(writer, p, lm);
					}
				}
				for (PageSequence s : v.getBody()) {
					LayoutMaster lm = s.getLayoutMaster();
					if (firstInVolume) {
						firstInVolume = false;
						writer.newVolume(lm);
					}
					writer.newSection(lm);
					for (Page p : s) {
						writePage(writer, p, lm);
					}				}
				for (PageSequence s : v.getPostVolumeContents()) {
					LayoutMaster lm = s.getLayoutMaster();
					if (firstInVolume) {
						firstInVolume = false;
						writer.newVolume(lm);
					}
					writer.newSection(lm);
					for (Page p : s) {
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
				int rowWidth = LayoutTools.length(chars)+row.getLeftMargin();
				String r = 	LayoutTools.fill(marginCharacter, margin) + chars;
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
