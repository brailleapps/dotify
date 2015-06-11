package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.daisy.dotify.api.formatter.CompoundField;
import org.daisy.dotify.api.formatter.CurrentPageField;
import org.daisy.dotify.api.formatter.Field;
import org.daisy.dotify.api.formatter.FieldList;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.MarkerReferenceField;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.api.writer.Row;
import org.daisy.dotify.common.text.StringTools;



/**
 * Provides a page object.
 * 
 * @author Joel Håkansson
 */
class PageImpl implements Page {
	private final static Pattern trailingWs = Pattern.compile("\\s*\\z");
	private final static Pattern softHyphen = Pattern.compile("\u00ad");
	private final PageSequenceBuilder parent;
	private final LayoutMaster master;
	private final FormatterContext fcontext;
	private final List<RowImpl> before;
	private final List<RowImpl> after;
	private final ArrayList<RowImpl> rows;
	private final ArrayList<RowImpl> pageArea;
	private final ArrayList<Marker> markers;
	private final ArrayList<String> anchors;
	private final ArrayList<String> identifiers;
	private final int pageIndex;
	private final int flowHeight;
	private int contentMarkersBegin;
	private boolean isVolBreak;
	private boolean isVolBreakAllowed;
	private int keepPreviousSheets;
	
	public PageImpl(LayoutMaster master, FormatterContext fcontext, PageSequenceBuilder parent, int pageIndex, List<RowImpl> before, List<RowImpl> after) {
		this.master = master;
		this.fcontext = fcontext;
		this.rows = new ArrayList<RowImpl>();
		this.before = before;
		this.after = after; 

		this.pageArea = new ArrayList<RowImpl>();
		this.markers = new ArrayList<Marker>();
		this.anchors = new ArrayList<String>();
		this.identifiers = new ArrayList<String>();
		this.pageIndex = pageIndex;
		contentMarkersBegin = 0;
		this.parent = parent;
		PageTemplate template = master.getTemplate(pageIndex+1);
		this.flowHeight = master.getPageHeight() - 
				(int)Math.ceil(getHeight(template.getHeader(), master.getRowSpacing())) -
				(int)Math.ceil(getHeight(template.getFooter(), master.getRowSpacing())) -
				(master.getBorder() != null ? (int)Math.ceil(distributeRowSpacing(null, false).spacing*2) : 0);
		this.isVolBreak = false;
		this.isVolBreakAllowed = true;
		this.keepPreviousSheets = 0;
	}
	
	static float getHeight(List<FieldList> list, float def) {
		float ret = 0;
		for (FieldList f : list) {
			if (f.getRowSpacing()!=null) {
				ret += f.getRowSpacing();
			} else {
				ret += def;
			}
		}
		return ret;
	}

	void addToPageArea(List<RowImpl> block) {
		pageArea.addAll(block);
	}
	
	public void newRow(RowImpl r) {
		if (rowsOnPage()==0) {
			contentMarkersBegin = markers.size();
		}
		rows.add(r);
		markers.addAll(r.getMarkers());
		anchors.addAll(r.getAnchors());
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
	
	public List<String> getAnchors() {
		return anchors;
	}
	
	public void addIdentifier(String id) {
		identifiers.add(id);
	}
	
	public List<String> getIdentifiers() {
		return identifiers;
	}
	
	/**
	 * Gets the page space needed to render the rows. 
	 * @param rows
	 * @param defSpacing a value >= 1.0
	 * @return returns the space, in rows
	 */
	static float rowsNeeded(Iterable<? extends Row> rows, float defSpacing) {
		float ret = 0;
		if (defSpacing < 1) {
			defSpacing = 1;
		}
		for (Row r : rows) {
			if (r.getRowSpacing()!=null && r.getRowSpacing()>=1) {
				ret += r.getRowSpacing();
			} else {
				ret += defSpacing;
			}
		}
		return ret;
	}
	
	float spaceNeeded() {
		return 	pageAreaSpaceNeeded() +
				rowsNeeded(rows, master.getRowSpacing());
	}
	
	float staticAreaSpaceNeeded() {
		return rowsNeeded(before, master.getRowSpacing()) + rowsNeeded(after, master.getRowSpacing());
	}
	
	float pageAreaSpaceNeeded() {
		return (pageArea.size()>0 ? staticAreaSpaceNeeded() + rowsNeeded(pageArea, master.getRowSpacing()) : 0);
	}
	
	/**
	 * Space needed if adding the supplied floating rows.
	 * @param rs
	 * @return
	 */
	float spaceNeeded(Iterable<? extends Row> rs) {
		return rowsNeeded(rs, master.getRowSpacing()) + (pageArea.size()==0 ? staticAreaSpaceNeeded() : 0);
	}
	
	int spaceUsedOnPage(int offs) {
		return (int)Math.ceil(spaceNeeded()) + offs;
	}

	public List<Row> getRows() {

		try {
			TextBorderStyle border = master.getBorder();
			if (border == null) {
				border = TextBorderStyle.NONE;
			}
			ArrayList<RowImpl> ret = new ArrayList<RowImpl>();
			{
				LayoutMaster lm = master;
				int pagenum = getPageIndex() + 1;
				PageTemplate t = lm.getTemplate(pagenum);
				BrailleTranslator filter = fcontext.getTranslator();
				ret.addAll(renderFields(lm, t.getHeader(), filter));
				if (lm.getPageArea()!=null && lm.getPageArea().getAlignment()==PageAreaProperties.Alignment.TOP && pageArea.size() > 0) {
					ret.addAll(before);
					ret.addAll(pageArea);
					ret.addAll(after);
				}
				ret.addAll(rows);
				float headerHeight = getHeight(t.getHeader(), lm.getRowSpacing());
				if (t.getFooter().size() > 0 || border != TextBorderStyle.NONE || (lm.getPageArea()!=null && lm.getPageArea().getAlignment()==PageAreaProperties.Alignment.BOTTOM && pageArea.size() > 0)) {
					float areaSize = (lm.getPageArea()!=null && lm.getPageArea().getAlignment()==PageAreaProperties.Alignment.BOTTOM ? pageAreaSpaceNeeded() : 0);
					while (Math.ceil(rowsNeeded(ret, lm.getRowSpacing()) + areaSize) < getFlowHeight() + headerHeight) {
						ret.add(new RowImpl());
					}
					if (lm.getPageArea()!=null && lm.getPageArea().getAlignment()==PageAreaProperties.Alignment.BOTTOM && pageArea.size() > 0) {
						ret.addAll(before);
						ret.addAll(pageArea);
						ret.addAll(after);
					}
					ret.addAll(renderFields(lm, t.getFooter(), filter));
				}
			}
			LayoutMaster lm = master;
			ArrayList<Row> ret2 = new ArrayList<Row>();
			{
				final int pagenum = getPageIndex() + 1;
				TextBorder tb = null;

				int fsize = border.getLeftBorder().length() + border.getRightBorder().length();
				final int pageMargin = ((pagenum % 2 == 0) ? lm.getOuterMargin() : lm.getInnerMargin());
				int w = master.getFlowWidth() + fsize + pageMargin;

				tb = new TextBorder.Builder(w, fcontext.getSpaceCharacter()+"")
						.style(border)
						.outerLeftMargin(pageMargin)
						.padToSize(!TextBorderStyle.NONE.equals(border))
						.build();
				if (!TextBorderStyle.NONE.equals(border)) {
					RowImpl r = new RowImpl(tb.getTopBorder());
					DistributedRowSpacing rs = distributeRowSpacing(lm.getRowSpacing(), true);
					r.setRowSpacing(rs.spacing);
					ret2.add(r);
				}
				String res;

				for (RowImpl row : ret) {
					res = "";
					if (row.getChars().length() > 0) {
						// remove trailing whitespace
						String chars = trailingWs.matcher(row.getChars()).replaceAll("");
						//if (!TextBorderStyle.NONE.equals(frame)) {
							res = tb.addBorderToRow(
									padLeft(master.getFlowWidth(), chars, row.getLeftMargin(), row.getRightMargin(), row.getAlignment()), 
									"");
						//} else {
						//	res = StringTools.fill(getMarginCharacter(), pageMargin + row.getLeftMargin()) + chars;
						//}
					} else {
						if (!TextBorderStyle.NONE.equals(border)) {
							res = tb.addBorderToRow(row.getLeftMargin().getContent(), row.getRightMargin().getContent());
						} else {
							if (!row.getLeftMargin().isSpaceOnly() || !row.getRightMargin().isSpaceOnly()) {
								res = TextBorder.addBorderToRow(
									lm.getFlowWidth(), row.getLeftMargin().getContent(), "", row.getRightMargin().getContent(), fcontext.getSpaceCharacter()+"");
							} else {
								res = "";
							}
						}
					}
					int rowWidth = StringTools.length(res) + pageMargin;
					String r = res;
					if (rowWidth > master.getPageWidth()) {
						throw new PaginatorException("Row is too long (" + rowWidth + "/" + master.getPageWidth() + ") '" + res + "'");
					}
					RowImpl r2 = new RowImpl(r);
					ret2.add(r2);
					Float rs2 = row.getRowSpacing();
					if (!TextBorderStyle.NONE.equals(border)) {
						DistributedRowSpacing rs = distributeRowSpacing(rs2, true);
						r2.setRowSpacing(rs.spacing);
						//don't add space to the last line
						if (row!=ret.get(ret.size()-1)) {
							RowImpl s = null;
							for (int i = 0; i < rs.lines-1; i++) {
								s = new RowImpl(tb.addBorderToRow(row.getLeftMargin().getContent(), row.getRightMargin().getContent()));
								s.setRowSpacing(rs.spacing);
								ret2.add(s);
							}
						}
					} else {
						r2.setRowSpacing(rs2);
					}
					
				}
				if (!TextBorderStyle.NONE.equals(border)) {
					ret2.add(new RowImpl(tb.getBottomBorder()));
				}
			}
			RowImpl last = ((RowImpl)ret2.get(ret2.size()-1));
			if (lm.getRowSpacing()!=1) {
				//set row spacing on the last row to 1.0
				last.setRowSpacing(1f);
			} else if (last.getRowSpacing()!=null) {
				//ignore row spacing on the last row if overall row spacing is 1.0
				last.setRowSpacing(null);
			}
			return ret2;
		} catch (PaginatorException e) {
			throw new RuntimeException("Cannot render header/footer", e);
		}
	}
	
	String padLeft(int w, String text, MarginProperties leftMargin, MarginProperties rightMargin, FormattingTypes.Alignment align) {
		if ("".equals(text) && leftMargin.isSpaceOnly() && rightMargin.isSpaceOnly()) {
			return "";
		} else {
			String r = leftMargin.getContent() + StringTools.fill(fcontext.getSpaceCharacter(), align.getOffset(w - (leftMargin.getContent().length() + rightMargin.getContent().length() + text.length()))) + text;
			if (rightMargin.isSpaceOnly()) {
				return r;
			} else {
				return r + StringTools.fill(fcontext.getSpaceCharacter(), w - r.length() - rightMargin.getContent().length()) + rightMargin.getContent();
			}
		}
	}

	/**
	 * Get the number for the page
	 * @return returns the page index in the sequence (zero based)
	 */
	public int getPageIndex() {
		return pageIndex;
	}

	public PageSequenceBuilder getParent() {
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
	
	
	private List<RowImpl> renderFields(LayoutMaster lm, List<FieldList> fields, BrailleTranslator translator) throws PaginatorException {
		ArrayList<RowImpl> ret = new ArrayList<RowImpl>();
		for (FieldList row : fields) {
			try {
				RowImpl r = new RowImpl(distribute(row, lm.getFlowWidth(), fcontext.getSpaceCharacter()+"", translator));
				r.setRowSpacing(row.getRowSpacing());
				ret.add(r);
			} catch (PaginatorToolsException e) {
				throw new PaginatorException("Error while rendering header", e);
			}
		}
		return ret;
	}
	
	private String distribute(FieldList chunks, int width, String padding, BrailleTranslator translator) throws PaginatorToolsException {
		ArrayList<String> chunkF = new ArrayList<String>();
		for (Field f : chunks.getFields()) {
			BrailleTranslatorResult btr = translator.translate(softHyphen.matcher(resolveField(f, this)).replaceAll(""));
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
	
	private static String resolveCurrentPageField(CurrentPageField f, PageImpl p) {
		int pagenum = p.getPageIndex() + 1;
		return f.getStyle().format(pagenum);
	}
	
	/**
	 * Divide a row-spacing value into several rows with a row-spacing < 2.
	 * <p>E.g. A row spacing of 2.5 will return:</p>
	 * <dl>
	 * 	<dt>RowSpacing.spacing</dt><dd>1.25</dd> 
	 *  <dt>RowSpacing.lines</dt><dd>2</dd>
	 * </dl>
	 * @param rs
	 * @return
	 */
	private DistributedRowSpacing distributeRowSpacing(Float rs, boolean nullIfEqualToDefault) {
		if (rs == null) {
			//use default
			rs = this.master.getRowSpacing();
		}
		int ins = Math.max((int)Math.floor(rs), 1);
		Float spacing = rs / ins;
		if (nullIfEqualToDefault && spacing.equals(this.master.getRowSpacing())) {
			return new DistributedRowSpacing(null, ins);
		} else {
			return new DistributedRowSpacing(spacing, ins);
		}
	}
	
	private class DistributedRowSpacing {
		private final Float spacing;
		private final int lines;
		DistributedRowSpacing(Float s, int l) {
			this.spacing = s;
			this.lines = l;
		}
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
