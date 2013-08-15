package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.formatter.BlockContentManager;
import org.daisy.dotify.formatter.CrossReferences;
import org.daisy.dotify.formatter.Leader;
import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.tools.RomanNumeral;
import org.daisy.dotify.translator.BrailleTranslatorResult;

class BlockContentManagerImpl implements BlockContentManager {
	private boolean isVolatile;
	private final ArrayList<Marker> groupMarkers;
	private final ArrayList<String> groupAnchors;
	private final Stack<Row> rows;
	private final CrossReferences refs;
	
	BlockContentManagerImpl(Stack<Segment> segments, RowDataProperties rdp, CrossReferences refs) {
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
		
		BlockHandler bh = new BlockHandler.Builder(
				rdp.getTranslator(),
				rdp.getMaster(),
				rdp.getMaster().getFlowWidth() - rdp.getRightMargin(),
				rdp.getRightMargin()).build();
		
		if (rdp.isList()) {
			bh.setListItem(rdp.getListLabel(), rdp.getListStyle());
		}
		for (Segment s : segments) {
			switch (s.getSegmentType()) {
				case NewLine:
				{
					//flush
					layout("", bh, ret, rdp, null);
					Row r = new Row("");
					r.setLeftMargin(((NewLineSegment)s).getLeftIndent());
					r.setRightMargin(rdp.getRightMargin());
					ret.add(r);
					break;
				}
				case Text:
				{
					TextSegment ts = (TextSegment)s;
					bh.setBlockProperties(ts.getBlockProperties());
					boolean oldValue = rdp.getTranslator().isHyphenating();
					rdp.getTranslator().setHyphenating(ts.getTextProperties().isHyphenating());
					layout(ts.getChars(), bh, ret, rdp, ts.getTextProperties().getLocale());
					rdp.getTranslator().setHyphenating(oldValue);
					break;
				}
				case Leader:
				{
					if (bh.getCurrentLeader()!=null) {
						layout("", bh, ret, rdp, null);
					}
					bh.setCurrentLeader((Leader)s);
					break;
				}
				case Reference:
				{
					isVolatile = true;
					PageNumberReferenceSegment rs = (PageNumberReferenceSegment)s;
					Integer page = null;
					if (refs!=null) {
						page = refs.getPageNumber(rs.getRefId());
					}
					//TODO: translate references using custom language?
					if (page==null) {
						layout("??", bh, ret, rdp, null);
					} else {
						switch (rs.getNumeralStyle()) {
							case ROMAN:
								layout(""+RomanNumeral.int2roman(page), bh, ret, rdp, null);
								break;
							case DEFAULT:default:
								layout(""+page, bh, ret, rdp, null);
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
			layout("", bh, ret, rdp, null);
		}
		return ret;
	}

	private void layout(CharSequence c, BlockHandler bh, Stack<Row> rows, RowDataProperties rdp, FilterLocale locale) {
		BrailleTranslatorResult btr;
		if (locale!=null) {
			try {
				btr = rdp.getTranslator().translate(c.toString(), locale);
			} catch (UnsupportedLocaleException e) {
				e.printStackTrace();
				btr = rdp.getTranslator().translate(c.toString());
			}
		} else {
			btr = rdp.getTranslator().translate(c.toString());
		}
		if (rows.size()==0) {
			rows.addAll(bh.layoutBlock(btr, rdp.getLeftMargin(), rdp.getBlockIndent(), rdp.getBlockIndentParent()));
		} else {
			rows.addAll(bh.appendBlock(btr, rdp.getLeftMargin(), rows.pop(), rdp.getBlockIndent(), rdp.getBlockIndentParent()));
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
