package org.daisy.dotify.formatter.impl.formatter;

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
import org.daisy.dotify.formatter.dom.RowDataProperties;
import org.daisy.dotify.formatter.dom.Segment;
import org.daisy.dotify.formatter.dom.TextSegment;
import org.daisy.dotify.formatter.utils.BlockHandler;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslatorResult;

class RowDataManagerImpl implements RowDataManager {
	private boolean isVolatile;
	private final ArrayList<Marker> groupMarkers;
	private final ArrayList<String> groupAnchors;
	private final Stack<Row> rows;
	private final CrossReferences refs;
	
	RowDataManagerImpl(Stack<Segment> segments, RowDataProperties rdp, CrossReferences refs) {
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
				rdp.getMaster().getFlowWidth() - rdp.getRightMargin()).build();
		
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
					PageNumberReference rs = (PageNumberReference)s;
					Page page = null;
					if (refs!=null) {
						page = refs.getPage(rs.getRefId());
					}
					//TODO: translate references using custom language?
					if (page==null) {
						layout("??", bh, ret, rdp, null);
					} else {
						int p = page.getPageIndex()+1;
						switch (rs.getNumeralStyle()) {
							case ROMAN:
								layout(""+RomanNumeral.int2roman(p), bh, ret, rdp, null);
								break;
							case DEFAULT:default:
								layout(""+p, bh, ret, rdp, null);
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
