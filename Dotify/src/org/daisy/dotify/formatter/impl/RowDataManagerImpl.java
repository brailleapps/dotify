package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.formatter.core.PageNumberReference;
import org.daisy.dotify.formatter.core.RomanNumeral;
import org.daisy.dotify.formatter.dom.AnchorSegment;
import org.daisy.dotify.formatter.dom.CrossReferences;
import org.daisy.dotify.formatter.dom.Leader;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.NewLineSegment;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.Row;
import org.daisy.dotify.formatter.dom.RowDataManager;
import org.daisy.dotify.formatter.dom.Segment;
import org.daisy.dotify.formatter.dom.TextSegment;
import org.daisy.dotify.formatter.utils.BlockHandler;

public class RowDataManagerImpl implements RowDataManager {
	private boolean isVolatile;
	private final ArrayList<Marker> groupMarkers;
	private final ArrayList<String> groupAnchors;
	private final Stack<Row> rows;
	private final CrossReferences refs;
	
	public RowDataManagerImpl(Stack<Segment> segments, RowDataProperties rdp, CrossReferences refs) {
		this.groupMarkers = new ArrayList<Marker>();
		this.groupAnchors = new ArrayList<String>();
		this.refs = refs;
		this.rows = calculateRows(segments, rdp);
	}
	
	/**
	 * Get markers that are not attached to a row, i.e. markers that proceeds any text contents
	 * @return returns markers that proceeds this FlowGroups text contents
	 */
	public ArrayList<Marker> getGroupMarkers() {
		return groupMarkers;
	}
	
	public ArrayList<String> getGroupAnchors() {
		return groupAnchors;
	}
	
	private Stack<Row> calculateRows(Stack<Segment> segments, RowDataProperties rdp) {
		isVolatile = false;
		Stack<Row> ret = new Stack<Row>();
		
		BlockHandler bh = new BlockHandler.Builder(rdp.getFilter(), rdp.getMaster()).build();
		if (rdp.isList()) {
			bh.setListItem(rdp.getListLabel(), rdp.getListStyle());
		}
		for (Segment s : segments) {
			switch (s.getSegmentType()) {
				case NewLine:
				{
					Row r = new Row("");
					r.setLeftMargin(((NewLineSegment)s).getLeftIndent());
					ret.add(r);
					break;
				}
				case Text:
				{
					TextSegment ts = (TextSegment)s;
					bh.setBlockProperties(ts.getBlockProperties());
					bh.setWidth(rdp.getMaster().getFlowWidth() - rdp.getRightMargin());
					layout(ts.getChars(), bh, ret, rdp.getLeftMargin(), rdp.getBlockIndent(), rdp.getBlockIndentParent());
					break;
				}
				case Leader:
				{
					if (bh.getCurrentLeader()!=null) {
						layout("", bh, ret, rdp.getLeftMargin(), rdp.getBlockIndent(), rdp.getBlockIndentParent());
					}
					bh.setCurrentLeader((Leader)s);
					break;
				}
				case Reference:
				{
					isVolatile = true;
					PageNumberReference rs = (PageNumberReference)s;
					Page page = null;
					if (refs!=null) {
						page = refs.getPage(rs.getRefId());
					}
					if (page==null) {
						layout("??", bh, ret, rdp.getLeftMargin(), rdp.getBlockIndent(), rdp.getBlockIndentParent());
					} else {
						int p = page.getPageIndex()+1;
						switch (rs.getNumeralStyle()) {
							case ROMAN:
								layout(""+RomanNumeral.int2roman(p), bh, ret, rdp.getLeftMargin(), rdp.getBlockIndent(), rdp.getBlockIndentParent());
								break;
							case DEFAULT:default:
								layout(""+p, bh, ret, rdp.getLeftMargin(), rdp.getBlockIndent(), rdp.getBlockIndentParent());
						}
					}
					break;
				}
				case Marker:
				{
					Marker m = (Marker)s;
					if (ret.isEmpty()) {
						groupMarkers.add(m);
					} else {
						ret.peek().addMarker(m);
					}
					break;
				}
				case Anchor:
				{
					AnchorSegment as = (AnchorSegment)s;
					if (segments.isEmpty()) {
						groupAnchors.add(as.getReferenceID());
					} else {
						ret.peek().addAnchor(as.getReferenceID());
					}
					break;
				}
			}
		}
		
		if (bh.getCurrentLeader()!=null || bh.getListItem()!=null) {
			bh.setWidth(rdp.getMaster().getFlowWidth() - rdp.getRightMargin());
			layout("", bh, ret, rdp.getLeftMargin(), rdp.getBlockIndent(), rdp.getBlockIndentParent());
		}
		return ret;
	}
	
	private void layout(CharSequence c, BlockHandler bh, Stack<Row> rows, int leftMargin, int blockIndent, int blockIndentParent) {
		if (rows.size()==0) {
			rows.addAll(bh.layoutBlock(c, leftMargin, blockIndent, blockIndentParent));
		} else {
			rows.addAll(bh.appendBlock(c, leftMargin, rows.pop(), blockIndent, blockIndentParent));
		}
	}

	public int getRowCount() {
		return rows.size();
	}

	public Iterator<Row> iterator() {
		return rows.iterator();
	}

	public boolean isVolatile() {
		return isVolatile;
	}

}
