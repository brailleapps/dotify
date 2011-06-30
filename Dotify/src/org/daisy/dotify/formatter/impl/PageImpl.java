package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.FormatterException;
import org.daisy.dotify.formatter.LayoutTools;
import org.daisy.dotify.formatter.LayoutToolsException;
import org.daisy.dotify.formatter.dom.CompoundField;
import org.daisy.dotify.formatter.dom.CurrentPageField;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.MarkerReferenceField;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.PageTemplate;
import org.daisy.dotify.formatter.dom.Row;
import org.daisy.dotify.text.StringFilter;



/**
 * Provides a page object.
 * 
 * @author Joel Håkansson
 */
class PageImpl implements Page {
	private PageSequenceImpl parent;
	private ArrayList<Row> rows;
	private ArrayList<Marker> markers;
	private final int pageIndex;
	private final PageTemplate template;
	private final int flowHeight;
	private int contentMarkersBegin;
	private boolean isVolBreak;
	
	public PageImpl(PageSequenceImpl parent, int pageIndex) {
		this.rows = new ArrayList<Row>();
		this.markers = new ArrayList<Marker>();
		this.pageIndex = pageIndex;
		contentMarkersBegin = 0;
		this.parent = parent;
		this.template = parent.getLayoutMaster().getTemplate(pageIndex+1);
		this.flowHeight = parent.getLayoutMaster().getPageHeight()-template.getHeaderHeight()-template.getFooterHeight();
		this.isVolBreak = false;
	}
	
	public void newRow(Row r) {
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
	
	public ArrayList<Row> getRows() {
		try {
			ArrayList<Row> ret = new ArrayList<Row>();
			LayoutMaster lm = getParent().getLayoutMaster();
			int pagenum = getPageIndex()+1;
			PageTemplate t = lm.getTemplate(pagenum);
			StringFilter filter = getParent().getFormatter().getDefaultFilter();
			ret.addAll(renderFields(lm, this, t.getHeader(), filter));
			ret.addAll(rows);
			if (t.getFooterHeight()>0) {
				while (ret.size()<lm.getPageHeight()-t.getFooterHeight()) {
					ret.add(new Row());
				}
				ret.addAll(renderFields(lm, this, t.getFooter(), filter));
			}
			return ret;
		} catch (FormatterException e) {
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

	public PageSequence getParent() {
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
	
	
	private static ArrayList<Row> renderFields(LayoutMaster lm, Page p, ArrayList<ArrayList<Object>> fields, StringFilter filters) throws FormatterException {
		ArrayList<Row> ret = new ArrayList<Row>();
		for (ArrayList<Object> row : fields) {
			try {
				ret.add(new Row(distribute(row, lm.getFlowWidth(), " ", p, filters)));
			} catch (LayoutToolsException e) {
				throw new FormatterException("Error while rendering header", e);
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
			int nextPage = page.getPageIndex() - page.getParent().getPageNumberOffset() + dir;
			//System.out.println("Next page: "+page.getPageIndex() + " | " + nextPage);
			if (nextPage < page.getParent().getPageCount() && nextPage >= 0) {
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
