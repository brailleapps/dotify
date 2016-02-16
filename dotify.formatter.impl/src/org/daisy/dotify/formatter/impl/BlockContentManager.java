package org.daisy.dotify.formatter.impl;

import java.util.Iterator;
import java.util.Stack;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.common.text.StringTools;

/**
 * BlockHandler is responsible for breaking blocks of text into rows. BlockProperties
 * such as list numbers, leaders and margins are resolved in the process. The input
 * text is filtered using the supplied StringFilter before breaking into rows, since
 * the length of the text could change.
 * 
 * @author Joel Håkansson
 */
class BlockContentManager extends AbstractBlockContentManager {
	private final static Pattern softHyphenPattern  = Pattern.compile("\u00ad");
	private final static Pattern trailingWsBraillePattern = Pattern.compile("[\\s\u2800]+\\z");

	private final Stack<RowImpl> rows;
	private final CrossReferences refs;
	private final int available;
	private final Context context;

	private Leader currentLeader;
	private ListItem item;
	
	BlockContentManager(int flowWidth, Stack<Segment> segments, RowDataProperties rdp, CrossReferences refs, Context context, FormatterContext fcontext) {
		super(flowWidth, rdp, fcontext);
		this.refs = refs;
		this.currentLeader = null;
		this.available = flowWidth - rightMargin.getContent().length();

		this.item = rdp.getListItem();
		
		this.rows = new Stack<>();
		this.context = context;

		calculateRows(segments);
	}
	
	public int getBlockHeight() {
		return getRowCount() + 
				rdp.getOuterSpaceBefore() + rdp.getInnerSpaceBefore() + 
				rdp.getOuterSpaceAfter() + rdp.getInnerSpaceAfter() + 
				(rdp.getLeadingDecoration()!=null?1:0)+
				(rdp.getTrailingDecoration()!=null?1:0);
	}
	
	public boolean isCollapsable() {
		return getRowCount() +
				rdp.getInnerSpaceAfter() +
				rdp.getInnerSpaceBefore() == 0 
				&&
				leftMargin.isSpaceOnly() &&
				rightMargin.isSpaceOnly() &&
				rdp.getLeadingDecoration()==null && rdp.getTrailingDecoration()==null;
	}
	
	private void calculateRows(Stack<Segment> segments) {
		isVolatile = false;
		
		for (Segment s : segments) {
			switch (s.getSegmentType()) {
				case NewLine:
				{
					//flush
					layout("", null, null);
					MarginProperties ret = new MarginProperties(leftMargin.getContent()+StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()), leftMargin.isSpaceOnly());
					rows.add(createAndConfigureEmptyNewRow(ret));
					break;
				}
				case Text:
				{
					TextSegment ts = (TextSegment)s;
					layout(
							Translatable.text(ts.getText()).
							locale(ts.getTextProperties().getLocale()).
							hyphenate(ts.getTextProperties().isHyphenating()).build(),
							ts.getTextProperties().getTranslationMode());
					break;
				}
				case Leader:
				{
					if (currentLeader!=null) {
						layout("", null, null);
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
						layout("??",  null, null);
					} else {
						layout("" + rs.getNumeralStyle().format(page), null, null);
					}
					break;
				}
				case Evaluate:
				{
					isVolatile = true;
					Evaluate e = (Evaluate)s;
					layout(Translatable.text(e.getExpression().render(context)).
							locale(e.getTextProperties().getLocale()).
							hyphenate(e.getTextProperties().isHyphenating()).
							build(), 
							null);
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
			layout("",  null, null);
		}
		if (rows.size()>0) {
			rows.get(0).addAnchors(0, groupAnchors);
			groupAnchors.clear();
			rows.get(0).addMarkers(0, groupMarkers);
			groupMarkers.clear();
		}
	}
	
	/**
	 * 
	 * @param margin rdp.getSpaceBefore()
	 * @return
	 *//*
	public List<RowImpl> getPreContentRows(int margin, Float marginRowSpacing) {
		List<RowImpl> preContentRows = new ArrayList<>();
		for (int i=0; i<margin;i++) {
			RowImpl row = new RowImpl("", leftParent, rightParent);
			//row.setAlignment(rdp.getAlignment());
			row.setRowSpacing(marginRowSpacing);
			preContentRows.add(row);
		}
		if (rdp.getLeadingDecoration()!=null) {
			preContentRows.add(makeDecorationRow(flowWidth, rdp.getLeadingDecoration(), leftParent, rightParent));
		}
		for (int i=0; i<rdp.getInnerSpaceBefore(); i++) {
			MarginProperties ret = new MarginProperties(leftMargin.getContent()+StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()), leftMargin.isSpaceOnly());
			preContentRows.add(createAndConfigureEmptyNewRow(ret));
		}

		return preContentRows;
	}*/

	public int getRowCount() {
		return rows.size();
	}

	@Override
	public Iterator<RowImpl> iterator() {
		return rows.iterator();
	}
	
	private void layout(String c, String locale, String mode) {
		layout(Translatable.text(c).locale(locale).build(), mode);
	}
	
	private void layout(Translatable spec, String mode) {
		try {
			layout(fcontext.getTranslator(mode).translate(spec), mode);
		} catch (TranslationException e) {
			throw new RuntimeException(e);
		}
	}

	private void layout(BrailleTranslatorResult btr, String mode) {
		// process first row, is it a new block or should we continue the current row?
		if (rows.size()==0) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listLabel;
				try {
					listLabel = fcontext.getTranslator(mode).translate(Translatable.text(item.getLabel()).build()).getTranslatedRemainder();
				} catch (TranslationException e) {
					throw new RuntimeException(e);
				}
				if (item.getType()==FormattingTypes.ListStyle.PL) {
					newRow(btr, listLabel, 0, rdp.getBlockIndentParent(), mode);
				} else {
					newRow(btr, listLabel, rdp.getFirstLineIndent(), rdp.getBlockIndent(), mode);
				}
				item = null;
			} else {
				newRow(btr, "", rdp.getFirstLineIndent(), rdp.getBlockIndent(), mode);
			}
		} else {
			RowImpl r  = rows.pop();
			newRow(new RowInfo("", r), btr, rdp.getBlockIndent(), mode);
		}
		while (btr.hasNext()) { //LayoutTools.length(chars.toString())>0
			newRow(btr, "", rdp.getTextIndent(), rdp.getBlockIndent(), mode);
		}
	}
	
	private void newRow(BrailleTranslatorResult chars, String contentBefore, int indent, int blockIndent, String mode) {
		newRow(new RowInfo(getPreText(contentBefore, indent, blockIndent), createAndConfigureEmptyNewRow(leftMargin)), chars, blockIndent, mode);
	}
	
	private String getPreText(String contentBefore, int indent, int blockIndent) {
		int thisIndent = indent + blockIndent - StringTools.length(contentBefore);
		//assert thisIndent >= 0;
		return contentBefore + StringTools.fill(fcontext.getSpaceCharacter(), thisIndent).toString();
	}

	//TODO: check leader functionality
	private void newRow(RowInfo m, BrailleTranslatorResult btr, int blockIndent, String mode) {
		// [margin][preContent][preTabText][tab][postTabText] 
		//      preContentPos ^
		String tabSpace = "";
		if (currentLeader!=null) {
			int leaderPos = currentLeader.getPosition().makeAbsolute(available);
			int offset = leaderPos-m.preTabPos;
			int align = getLeaderAlign(currentLeader, btr.countRemaining());
			
			if (m.preTabPos>leaderPos || offset - align < 0) { // if tab position has been passed or if text does not fit within row, try on a new row
				rows.add(m.row);
				m = new RowInfo(StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()+blockIndent), createAndConfigureEmptyNewRow(m.row.getLeftMargin()));
				//update offset
				offset = leaderPos-m.preTabPos;
			}
			tabSpace = buildLeader(offset - align, mode);
		}
		breakNextRow(m, btr, tabSpace);
	}

	private String buildLeader(int len, String mode) {
		try {
			if (len > 0) {
				String leaderPattern;
				try {
					leaderPattern = fcontext.getTranslator(mode).translate(Translatable.text(currentLeader.getPattern()).build()).getTranslatedRemainder();
				} catch (TranslationException e) {
					throw new RuntimeException(e);
				}
				return StringTools.fill(leaderPattern, len);
			} else {
				Logger.getLogger(this.getClass().getCanonicalName())
					.fine("Leader position has been passed on an empty row or text does not fit on an empty row, ignoring...");
				return "";
			}
		} finally {
			// always discard leader
			currentLeader = null;
		}
	}

	private void breakNextRow(RowInfo m, BrailleTranslatorResult btr, String tabSpace) {
		int contentLen = StringTools.length(tabSpace) + m.preTabTextLen;
		boolean force = contentLen == 0;
		//don't know if soft hyphens need to be replaced, but we'll keep it for now
		String next = softHyphenPattern.matcher(btr.nextTranslatedRow(m.maxLenText - contentLen, force)).replaceAll("");
		if ("".equals(next) && "".equals(tabSpace)) {
			m.row.setChars(m.preContent + trailingWsBraillePattern.matcher(m.preTabText).replaceAll(""));
			rows.add(m.row);
		} else {
			m.row.setChars(m.preContent + m.preTabText + tabSpace + next);
			m.row.setLeaderSpace(m.row.getLeaderSpace()+tabSpace.length());
			rows.add(m.row);
		}
	}
	
	private static int getLeaderAlign(Leader leader, int length) {
		switch (leader.getAlignment()) {
			case LEFT:
				return 0;
			case RIGHT:
				return length;
			case CENTER:
				return length/2;
		}
		return 0;
	}
	
	private class RowInfo {
		final String preTabText;
		final int preTabTextLen;
		final String preContent;
		final int preTabPos;
		final int maxLenText;
		final RowImpl row;
		private RowInfo(String preContent, RowImpl r) {
			this.preTabText = r.getChars();
			this.row = r;
			this.preContent = preContent;
			int preContentPos = r.getLeftMargin().getContent().length()+StringTools.length(preContent);
			this.preTabTextLen = StringTools.length(preTabText);
			this.preTabPos = preContentPos+preTabTextLen;
			this.maxLenText = available-(preContentPos);
			if (this.maxLenText<1) {
				throw new RuntimeException("Cannot continue layout: No space left for characters.");
			}
		}
	}

}
