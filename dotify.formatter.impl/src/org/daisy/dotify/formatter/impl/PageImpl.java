package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.CompoundField;
import org.daisy.dotify.api.formatter.CurrentPageField;
import org.daisy.dotify.api.formatter.Field;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.MarkerReferenceField;
import org.daisy.dotify.api.formatter.Page;
import org.daisy.dotify.api.formatter.PageTemplate;
import org.daisy.dotify.api.formatter.Row;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.tools.StringTools;



/**
 * Provides a page object.
 * 
 * @author Joel Håkansson
 */
class PageImpl implements Page {
	private String marginCharacter = null;
	private PageSequenceImpl parent;
	private ArrayList<RowImpl> rows;
	private ArrayList<Marker> markers;
	private final int pageIndex;
	private final int flowHeight;
	private int contentMarkersBegin;
	private boolean isVolBreak;
	private boolean isVolBreakAllowed;
	private int keepPreviousSheets;
	
	public PageImpl(PageSequenceImpl parent, int pageIndex) {
		this.rows = new ArrayList<RowImpl>();
		this.markers = new ArrayList<Marker>();
		this.pageIndex = pageIndex;
		contentMarkersBegin = 0;
		this.parent = parent;
		PageTemplate template = parent.getLayoutMaster().getTemplate(pageIndex+1);
		this.flowHeight = parent.getLayoutMaster().getPageHeight() - template.getHeaderHeight() - template.getFooterHeight() - (parent.getLayoutMaster().getFrame() != null ? 2 : 0);
		this.isVolBreak = false;
		this.isVolBreakAllowed = true;
		this.keepPreviousSheets = 0;
	}
	
	public void newRow(RowImpl r) {
		if (rowsOnPage()==0) {
			contentMarkersBegin = markers.size();
		}
		rows.add(r);
		markers.addAll(r.getMarkers());
	}
	
	/**
	 * Gets the number of rows on this page
	 * @return returns the number of rows on this page
	 */
	public int rowsOnPage() {
		return rows.size();
	}
	
	public void addMarkers(List<Marker> m) {
		markers.addAll(m);
	}
	
	/**
	 * Get all markers for this page
	 * @return returns a list of all markers on a page
	 */
	public List<Marker> getMarkers() {
		return markers;
	}
	
	/**
	 * Get markers for this page excluding markers before text content
	 * @return returns a list of markers on a page
	 */
	public List<Marker> getContentMarkers() {
		return markers.subList(contentMarkersBegin, markers.size());
	}
	
	private String getMarginCharacter() {
		// lazy init
		if (marginCharacter == null) {
			marginCharacter = getParent().getTranslator().translate(" ").getTranslatedRemainder();
		}
		return marginCharacter;
	}

	public List<Row> getRows() {

		try {
			TextBorderStyle frame = getParent().getLayoutMaster().getFrame();
			if (frame == null) {
				frame = TextBorderStyle.NONE;
			}
			ArrayList<RowImpl> ret = new ArrayList<RowImpl>();
			{
				LayoutMaster lm = getParent().getLayoutMaster();
				int pagenum = getPageIndex() + 1;
				PageTemplate t = lm.getTemplate(pagenum);
				BrailleTranslator filter = getParent().getTranslator();
				ret.addAll(renderFields(lm, t.getHeader(), filter));
				ret.addAll(rows);
				if (t.getFooterHeight() > 0 || frame != TextBorderStyle.NONE) {
					while (ret.size() < getFlowHeight() + t.getHeaderHeight()) {
						ret.add(new RowImpl());
					}
					ret.addAll(renderFields(lm, t.getFooter(), filter));
				}
			}
			ArrayList<Row> ret2 = new ArrayList<Row>();
			{
				final int pagenum = getPageIndex() + 1;
				LayoutMaster lm = getParent().getLayoutMaster();
				TextBorder tb = null;

				int fsize = frame.getLeftBorder().length() + frame.getRightBorder().length();
				final int pageMargin = ((pagenum % 2 == 0) ? lm.getOuterMargin() : lm.getInnerMargin());
				int w = getParent().getLayoutMaster().getFlowWidth() + fsize + pageMargin;

				tb = new TextBorder.Builder(w, getMarginCharacter())
						.style(frame)
						.outerLeftMargin(StringTools.fill(getMarginCharacter(), pageMargin))
						.build();
				if (!TextBorderStyle.NONE.equals(frame)) {
					ret2.add(new RowImpl(tb.getTopBorder()));
				}
				String res;

				for (RowImpl row : ret) {
					res = "";
					if (row.getChars().length() > 0) {
						// remove trailing whitespace
						String chars = row.getChars().replaceAll("\\s*\\z", "");
						//if (!TextBorderStyle.NONE.equals(frame)) {
							res = tb.addBorderToRow(chars, 
									TextBorder.Align.valueOf(row.getAlignment().toString()), 
									StringTools.fill(getMarginCharacter(), row.getLeftMargin()), 
									StringTools.fill(getMarginCharacter(), row.getRightMargin()),
									TextBorderStyle.NONE.equals(frame));
						//} else {
						//	res = StringTools.fill(getMarginCharacter(), pageMargin + row.getLeftMargin()) + chars;
						//}
					} else {
						if (!TextBorderStyle.NONE.equals(frame)) {
							res = tb.addBorderToRow("", 
									TextBorder.Align.valueOf(row.getAlignment().toString()),
									StringTools.fill(getMarginCharacter(), row.getLeftMargin()), 
									StringTools.fill(getMarginCharacter(), row.getRightMargin()), false);
						} else {
							res = "";
						}
					}
					int rowWidth = StringTools.length(res) + pageMargin;
					String r = res;
					if (rowWidth > getParent().getLayoutMaster().getPageWidth()) {
						throw new PaginatorException("Row is too long (" + rowWidth + "/" + getParent().getLayoutMaster().getPageWidth() + ") '" + res + "'");
					}
					ret2.add(new RowImpl(r));
				}
				if (!TextBorderStyle.NONE.equals(frame)) {
					ret2.add(new RowImpl(tb.getBottomBorder()));
				}
			}
			return ret2;
		} catch (PaginatorException e) {
			throw new RuntimeException("Cannot render header/footer", e);
		}
	}

	/**
	 * Get the number for the page
	 * @return returns the page index in the sequence (zero based)
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	public PageSequenceImpl getParent() {
		return parent;
	}
	
	/**
	 * Gets the flow height for this page, i.e. the number of rows available for text flow
	 * @return returns the flow height
	 */
	public int getFlowHeight() {
		return flowHeight;
	}

	public boolean isVolumeBreak() {
		return isVolBreak;
	}

	public void setVolumeBreak(boolean value) {
		isVolBreak = value;
	}
	
	
	private List<RowImpl> renderFields(LayoutMaster lm, List<List<Field>> fields, BrailleTranslator translator) throws PaginatorException {
		ArrayList<RowImpl> ret = new ArrayList<RowImpl>();
		for (List<Field> row : fields) {
			try {
				ret.add(new RowImpl(distribute(row, lm.getFlowWidth(), translator.translate(" ").getTranslatedRemainder(), translator)));
			} catch (PaginatorToolsException e) {
				throw new PaginatorException("Error while rendering header", e);
			}
		}
		return ret;
	}
	
	private String distribute(List<Field> chunks, int width, String padding, BrailleTranslator translator) throws PaginatorToolsException {
		ArrayList<String> chunkF = new ArrayList<String>();
		for (Field f : chunks) {
			BrailleTranslatorResult btr = translator.translate(resolveField(f, this).replaceAll("\u00ad", ""));
			chunkF.add(btr.getTranslatedRemainder());
		}
		return PaginatorTools.distribute(chunkF, width, padding, PaginatorTools.DistributeMode.EQUAL_SPACING);
	}
	
	private static String resolveField(Field field, PageImpl p) {
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

	private static String resolveCompoundField(CompoundField f, PageImpl p) {
		StringBuffer sb = new StringBuffer();
		for (Field f2 : f) {
			sb.append(resolveField(f2, p));
		}
		return sb.toString();
	}

	private static String findMarker(PageImpl page, MarkerReferenceField markerRef) {
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
			int nextPage = page.getPageIndex() - page.getParent().getPageNumberOffset() + dir;
			//System.out.println("Next page: "+page.getPageIndex() + " | " + nextPage);
			if (nextPage < page.getParent().getPageCount() && nextPage >= 0) {
				PageImpl next = page.getParent().getPage(nextPage);
				return findMarker(next, markerRef);
			}
		}
		return "";
	}
	
	private static String resolveCurrentPageField(CurrentPageField f, Page p) {
		//TODO: include page number offset?
		int pagenum = p.getPageIndex() + 1;
		return NumeralStyleFormatter.format(pagenum, f.getStyle());
	}
	
	void setKeepWithPreviousSheets(int value) {
		keepPreviousSheets = Math.max(value, keepPreviousSheets);
	}
	
	void setAllowsVolumeBreak(boolean value) {
		this.isVolBreakAllowed = value;
	}

	public boolean allowsVolumeBreak() {
		return isVolBreakAllowed;
	}

	public int keepPreviousSheets() {
		return keepPreviousSheets;
	}

}
