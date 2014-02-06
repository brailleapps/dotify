package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.tools.StringTools;

/**
 * BlockHandler is responsible for breaking blocks of text into rows. BlockProperties
 * such as list numbers, leaders and margins are resolved in the process. The input
 * text is filtered using the supplied StringFilter before breaking into rows, since
 * the length of the text could change.
 * 
 * @author Joel Håkansson
 */
class BlockContentManagerImpl implements BlockContentManager {
	private boolean isVolatile;
	private final ArrayList<Marker> groupMarkers;
	private final ArrayList<String> groupAnchors;
	private final Stack<RowImpl> rows;
	private final CrossReferences refs;
	
	private final RowDataProperties rdp;
	private final int available;
	private final MarginProperties rightMargin;
	private final Context context;
	private final FormatterContext fcontext;
	
	private Leader currentLeader;

	private ListItem item;
	
	BlockContentManagerImpl(int flowWidth, Stack<Segment> segments, RowDataProperties rdp, CrossReferences refs, Context context, FormatterContext fcontext) {
		this.groupMarkers = new ArrayList<Marker>();
		this.groupAnchors = new ArrayList<String>();
		this.refs = refs;
		
		this.currentLeader = null;

		this.rdp = rdp;
		this.available = flowWidth - rdp.getRightMargin().getContent().length();
		this.rightMargin = rdp.getRightMargin();
		this.item = rdp.getListItem();
		
		this.rows = new Stack<RowImpl>();
		this.context = context;
		this.fcontext = fcontext;
		calculateRows(segments);
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
	
	private void calculateRows(Stack<Segment> segments) {
		isVolatile = false;
		
		for (Segment s : segments) {
			switch (s.getSegmentType()) {
				case NewLine:
				{
					//flush
					layout("", null);
					RowImpl r = new RowImpl("");
					r.setLeftMargin(((NewLineSegment)s).getLeftIndent());
					r.setRightMargin(rdp.getRightMargin());
					rows.add(r);
					break;
				}
				case Text:
				{
					TextSegment ts = (TextSegment)s;
					boolean oldValue = fcontext.getTranslator().isHyphenating();
					fcontext.getTranslator().setHyphenating(ts.getTextProperties().isHyphenating());
					layout(ts.getText(), ts.getTextProperties().getLocale());
					fcontext.getTranslator().setHyphenating(oldValue);
					break;
				}
				case Leader:
				{
					if (currentLeader!=null) {
						layout("",   null);
					}
					currentLeader= (Leader)s;
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
						layout("??",  null);
					} else {
						layout("" + rs.getNumeralStyle().format(page), null);
					}
					break;
				}
				case Evaluate:
				{
					isVolatile = true;
					Evaluate e = (Evaluate)s;
					boolean oldValue = fcontext.getTranslator().isHyphenating();
					fcontext.getTranslator().setHyphenating(e.getTextProperties().isHyphenating());
					layout(e.getExpression().render(context), e.getTextProperties().getLocale());
					fcontext.getTranslator().setHyphenating(oldValue);
					break;
				}
				case Marker:
				{
					Marker m = (Marker)s;
					if (rows.isEmpty()) {
						groupMarkers.add(m);
					} else {
						rows.peek().addMarker(m);
					}
					break;
				}
				case Anchor:
				{
					AnchorSegment as = (AnchorSegment)s;
					if (rows.isEmpty()) {
						groupAnchors.add(as.getReferenceID());
					} else {
						rows.peek().addAnchor(as.getReferenceID());
					}
					break;
				}
			}
		}
		
		if (currentLeader!=null || item!=null) {
			layout("",  null);
		}
	}

	public int getRowCount() {
		return rows.size();
	}

	public Iterator<RowImpl> iterator() {
		return rows.iterator();
	}

	public boolean isVolatile() {
		return isVolatile;
	}

	private void layout(CharSequence c, String locale) {
		BrailleTranslatorResult btr = getTranslatedResult(c, locale);
		// process first row, is it a new block or should we continue the current row?
		if (rows.size()==0) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listLabel = fcontext.getTranslator().translate(item.getLabel()).getTranslatedRemainder();
				if (item.getType()==FormattingTypes.ListStyle.PL) {
					newRow(listLabel, btr, rdp.getLeftMargin(), 0, rdp.getBlockIndentParent());
				} else {
					newRow(listLabel, btr, rdp.getLeftMargin(), rdp.getFirstLineIndent(), rdp.getBlockIndent());
				}
				item = null;
			} else {
				newRow("", btr, rdp.getLeftMargin(), rdp.getFirstLineIndent(), rdp.getBlockIndent());
			}
		} else {
			RowImpl r  = rows.pop();
			newRow(r.getMarkers(), r.getLeftMargin(), "", r.getChars().toString(), btr, rdp.getBlockIndent());
		}
		while (btr.hasNext()) { //LayoutTools.length(chars.toString())>0
			newRow("", btr, rdp.getLeftMargin(), rdp.getTextIndent(), rdp.getBlockIndent());
		}
	}
	
	private BrailleTranslatorResult getTranslatedResult(CharSequence c, String locale) {
		BrailleTranslatorResult btr;
		if (locale!=null) {
			try {
				btr = fcontext.getTranslator().translate(c.toString(), locale);
			} catch (TranslationException e) {
				Logger.getLogger(this.getClass().getCanonicalName())
					.log(Level.WARNING, "Failed to translate using the specified locale: " + locale + ". Using default", e);
				btr = fcontext.getTranslator().translate(c.toString());
			}
		} else {
			btr = fcontext.getTranslator().translate(c.toString());
		}
		return btr;
	}

	private void newRow(String contentBefore, BrailleTranslatorResult chars, MarginProperties margin, int indent, int blockIndent) {
		int thisIndent = indent + blockIndent - StringTools.length(contentBefore);
		//assert thisIndent >= 0;
		String preText = contentBefore + StringTools.fill(fcontext.getSpaceCharacter(), thisIndent).toString();
		newRow(null, margin, preText, "", chars, blockIndent);
	}

	//TODO: check leader functionality
	private void newRow(List<Marker> r, MarginProperties margin, String preContent, String preTabText, BrailleTranslatorResult btr, int blockIndent) {

		// [margin][preContent][preTabText][tab][postTabText] 
		//      preContentPos ^

		int preTextIndent = StringTools.length(preContent);
		int preContentPos = margin.getContent().length()+preTextIndent;
		preTabText = preTabText.replaceAll("\u00ad", "");
		int preTabPos = preContentPos+StringTools.length(preTabText);
		int postTabTextLen = btr.countRemaining();
		int maxLenText = available-(preContentPos);
		if (maxLenText<1) {
			throw new RuntimeException("Cannot continue layout: No space left for characters.");
		}

		String tabSpace = "";
		if (currentLeader!=null) {
			int leaderPos = currentLeader.getPosition().makeAbsolute(available);
			int offset = leaderPos-preTabPos;
			int align = 0;
			switch (currentLeader.getAlignment()) {
				case LEFT:
					align = 0;
					break;
				case RIGHT:
					align = postTabTextLen;
					break;
				case CENTER:
					align = postTabTextLen/2;
					break;
			}
			if (preTabPos>leaderPos || offset - align < 0) { // if tab position has been passed or if text does not fit within row, try on a new row
				RowImpl row = new RowImpl(preContent + preTabText);
				row.setLeftMargin(margin);
				row.setRightMargin(rightMargin);
				row.setAlignment(rdp.getAlignment());
				row.setRowSpacing(rdp.getRowSpacing());
				if (r!=null) {
					row.addMarkers(r);
					r = null;
				}
				rows.add(row);

				preContent = StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()+blockIndent);
				preTextIndent = StringTools.length(preContent);
				preTabText = "";
				
				preContentPos = margin.getContent().length()+preTextIndent;
				preTabPos = preContentPos;
				maxLenText = available-(preContentPos);
				offset = leaderPos-preTabPos;
			}
			if (offset - align > 0) {
				String leaderPattern = fcontext.getTranslator().translate(currentLeader.getPattern()).getTranslatedRemainder();
				tabSpace = StringTools.fill(leaderPattern, offset - align);
			} else {
				Logger.getLogger(this.getClass().getCanonicalName())
					.fine("Leader position has been passed on an empty row or text does not fit on an empty row, ignoring...");
			}
		}

		maxLenText -= StringTools.length(tabSpace);
		maxLenText -= StringTools.length(preTabText);

		boolean force = maxLenText >= available - (preContentPos);
		String next = btr.nextTranslatedRow(maxLenText, force);
		RowImpl nr;
		if ("".equals(next) && "".equals(tabSpace)) {
			nr = new RowImpl(preContent + preTabText.replaceAll("[\\s\u2800]+\\z", ""));
		} else {
			nr = new RowImpl(preContent + preTabText + tabSpace + next);
		}
		
		// discard leader
		currentLeader = null;

		assert nr != null;
		if (r!=null) {
			nr.addMarkers(r);
		}
		nr.setLeftMargin(margin);
		nr.setRightMargin(rightMargin);
		nr.setAlignment(rdp.getAlignment());
		nr.setRowSpacing(rdp.getRowSpacing());
		/*
		if (nr.getChars().length()>master.getFlowWidth()) {
			throw new RuntimeException("Row is too long (" + nr.getChars().length() + "/" + master.getFlowWidth() + ") '" + nr.getChars() + "'");
		}*/
		rows.add(nr);
	}

}
