package org.daisy.dotify.system.tasks.layout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.LayoutException;
import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.system.tasks.layout.impl.Page;
import org.daisy.dotify.system.tasks.layout.impl.PageSequence;
import org.daisy.dotify.system.tasks.layout.impl.field.CompoundField;
import org.daisy.dotify.system.tasks.layout.impl.field.CurrentPageField;
import org.daisy.dotify.system.tasks.layout.impl.field.MarkerReferenceField;
import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;
import org.daisy.dotify.system.tasks.layout.page.PageStruct;
import org.daisy.dotify.system.tasks.layout.page.PagedMediaWriter;
import org.daisy.dotify.system.tasks.layout.page.Template;
import org.daisy.dotify.system.tasks.layout.text.StringFilter;
import org.daisy.dotify.system.tasks.layout.utils.LayoutTools;
import org.daisy.dotify.system.tasks.layout.utils.LayoutToolsException;

/**
 * Provides a method for writing pages to a PagedMediaWriter,
 * adding headers and footers as required by the layout.
 * @author Joel Håkansson
 */
public class WriterHandler {
	private static final Character SPACE_CHAR = ' '; //'\u2800'
	
	/**
	 * Writes this structure to the suppled PagedMediaWriter adding headers and footers,
	 * where needed.
	 * @param writer the PagedMediaWriter to write to
	 * @throws IOException if IO fails
	 */
	public static void write(PageStruct ps, PagedMediaWriter writer) throws IOException {
		try {
			int rowNo = 1;
			for (PageSequence s : ps) {
				LayoutMaster lm = s.getLayoutMaster();
				writer.newSection(lm);
				for (Page p : s) {
					writer.newPage();
					int pagenum = p.getPageIndex()+1;
					Template t = lm.getTemplate(pagenum);
					//p.setHeader(renderFields(lm, p, t.getHeader()));
					//p.setFooter(renderFields(lm, p, t.getFooter()));
					ArrayList<Row> rows = new ArrayList<Row>();
					rows.addAll(renderFields(lm, p, t.getHeader(), ps.getFilter()));
					rows.addAll(p.getRows());
					if (t.getFooterHeight()>0) {
						while (rows.size()<lm.getPageHeight()-t.getFooterHeight()) {
							rows.add(new Row());
						}
						rows.addAll(renderFields(lm, p, t.getFooter(), ps.getFilter()));
					}
					for (Row row : rows) {
						if (row.getChars().length()>0) {
							int margin = ((pagenum % 2 == 0) ? lm.getOuterMargin() : lm.getInnerMargin()) + row.getLeftMargin();
							// remove trailing whitespace
							String chars = row.getChars().replaceAll("\\s*\\z", "");
							// add left margin
							int rowWidth = LayoutTools.length(chars)+row.getLeftMargin();
							String r = 	LayoutTools.fill(SPACE_CHAR, margin) + chars;
							if (rowWidth>lm.getFlowWidth()) {
								throw new LayoutException("Row no " + rowNo + " is too long (" + rowWidth + "/" + lm.getFlowWidth() + ") '" + chars + "'");
							}
							writer.newRow(r);
						} else {
							writer.newRow();
						}
						rowNo++;
					}
				}
			}
		} catch (LayoutException e) {
			IOException ex = new IOException("Layout exception");
			ex.initCause(e);
			throw ex;
		}
	}
	
	private static ArrayList<Row> renderFields(LayoutMaster lm, Page p, ArrayList<ArrayList<Object>> fields, StringFilter filters) throws LayoutException {
		ArrayList<Row> ret = new ArrayList<Row>();
		for (ArrayList<Object> row : fields) {
			try {
				ret.add(new Row(distribute(row, lm.getFlowWidth(), " ", p, filters)));
			} catch (LayoutToolsException e) {
				throw new LayoutException("Error while rendering header", e);
			}
		}
		return ret;
	}
	
	private static String distribute(ArrayList<Object> chunks, int width, String padding, Page p, StringFilter filters) throws LayoutToolsException {
		ArrayList<String> chunkF = new ArrayList<String>();
		for (Object f : chunks) {
			chunkF.add(filters.filter(resolveField(f, p).replaceAll("\u00ad", "")));
		}
		return LayoutTools.distribute(chunkF, width, padding, LayoutTools.DistributeMode.EQUAL_SPACING);
	}
	
	private static String resolveField(Object field, Page p) {
		if (field instanceof CompoundField) {
			return resolveCompoundField((CompoundField)field, p);
		} else if (field instanceof MarkerReferenceField) {
			MarkerReferenceField f2 = (MarkerReferenceField)field;
			return findMarker(p, f2);
		} else if (field instanceof CurrentPageField) {
			return resolveCurrentPageField((CurrentPageField)field, p);
		} else {
			return field.toString();
		}
	}

	private static String resolveCompoundField(CompoundField f, Page p) {
		StringBuffer sb = new StringBuffer();
		for (Object f2 : f) {
			sb.append(resolveField(f2, p));
		}
		return sb.toString();
	}

	public static String findMarker(Page page, MarkerReferenceField markerRef) {
		int dir = 1;
		int index = 0;
		int count = 0;
		List<Marker> m;
		if (markerRef.getSearchScope() == MarkerReferenceField.MarkerSearchScope.PAGE_CONTENT) {
			m = page.getContentMarkers();
		} else {
			m = page.getMarkers();
		}
		if (markerRef.getSearchDirection() == MarkerReferenceField.MarkerSearchDirection.BACKWARD) {
			dir = -1;
			index = m.size()-1;
		}
		while (count < m.size()) {
			Marker m2 = m.get(index);
			if (m2.getName().equals(markerRef.getName())) {
				return m2.getValue();
			}
			index += dir; 
			count++;
		}
		if (markerRef.getSearchScope() == MarkerReferenceField.MarkerSearchScope.SEQUENCE) {
			int nextPage = page.getPageIndex() - page.getParent().getOffset() + dir;
			//System.out.println("Next page: "+page.getPageIndex() + " | " + nextPage);
			if (nextPage < page.getParent().getSize() && nextPage >= 0) {
				Page next = page.getParent().getPage(nextPage);
				return findMarker(next, markerRef);
			}
		}
		return "";
	}

	private static String resolveCurrentPageField(CurrentPageField f, Page p) {
		int pagenum = p.getPageIndex() + 1;
		return f.style(pagenum);
	}
}
