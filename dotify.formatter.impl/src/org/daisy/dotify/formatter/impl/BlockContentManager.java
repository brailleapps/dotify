package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.translator.BrailleTranslatorResult;
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
class BlockContentManager implements Iterable<RowImpl> {
	private final static Pattern softHyphenPattern  = Pattern.compile("\u00ad");
	private final static Pattern trailingWsBraillePattern = Pattern.compile("[\\s\u2800]+\\z");

	private boolean isVolatile;
	private final ArrayList<Marker> groupMarkers;
	private final ArrayList<String> groupAnchors;
	private final Stack<RowImpl> rows;
	private final CrossReferences refs;
	
	private final RowDataProperties rdp;
	private final int available;

	private final Context context;
	private final FormatterContext fcontext;
	private final MarginProperties leftMargin;
	private final MarginProperties rightMargin;
	private final MarginProperties leftParent;
	private final MarginProperties rightParent;
	private Leader currentLeader;
	private List<RowImpl> preContentRows;
	private List<RowImpl> postContentRows;
	private List<RowImpl> skippablePostContentRows;

	private ListItem item;
	
	BlockContentManager(int flowWidth, Stack<Segment> segments, RowDataProperties rdp, CrossReferences refs, Context context, FormatterContext fcontext) {
		this.groupMarkers = new ArrayList<Marker>();
		this.groupAnchors = new ArrayList<String>();
		this.refs = refs;
		this.fcontext = fcontext;
		this.currentLeader = null;

		this.rdp = rdp;
		this.leftMargin = rdp.getLeftMargin().buildMargin(fcontext.getSpaceCharacter());
		this.rightMargin = rdp.getRightMargin().buildMargin(fcontext.getSpaceCharacter());
		this.available = flowWidth - rightMargin.getContent().length();

		this.item = rdp.getListItem();
		
		this.rows = new Stack<RowImpl>();
		this.context = context;
		this.preContentRows = new ArrayList<RowImpl>();
		this.leftParent = rdp.getLeftMargin().buildMarginParent(fcontext.getSpaceCharacter());
		this.rightParent = rdp.getRightMargin().buildMarginParent(fcontext.getSpaceCharacter());
		for (int i=0; i<rdp.getSpaceBefore();i++) {
			RowImpl row = new RowImpl("", leftParent, rightParent);
			row.setAlignment(rdp.getAlignment());
			row.setRowSpacing(rdp.getRowSpacing());
			preContentRows.add(row);
		}
		if (rdp.getLeadingDecoration()!=null) {
			preContentRows.add(makeDecorationRow(flowWidth, rdp.getLeadingDecoration(), leftParent, rightParent));
		}
		this.postContentRows = new ArrayList<RowImpl>();

		if (rdp.getTrailingDecoration()!=null) {
			postContentRows.add(makeDecorationRow(flowWidth, rdp.getTrailingDecoration(), leftParent, rightParent));
		}
		
		this.skippablePostContentRows = new ArrayList<RowImpl>();
		for (int i=0; i<rdp.getSpaceAfter();i++) {
			RowImpl row = new RowImpl("", leftParent, rightParent);
			row.setAlignment(rdp.getAlignment());
			row.setRowSpacing(rdp.getRowSpacing());
			skippablePostContentRows.add(row);
		}

		calculateRows(segments);
	}

	private RowImpl makeDecorationRow(int flowWidth, SingleLineDecoration d, MarginProperties leftParent, MarginProperties rightParent) {
		int w = flowWidth - rightParent.getContent().length() - leftParent.getContent().length();
		int aw = w-d.getLeftCorner().length()-d.getRightCorner().length();
		RowImpl row = new RowImpl(d.getLeftCorner() + StringTools.fill(d.getLinePattern(), aw) + d.getRightCorner());
		row.setLeftMargin(leftParent);
		row.setRightMargin(rightParent);
		row.setAlignment(rdp.getAlignment());
		row.setRowSpacing(rdp.getRowSpacing());
		return row;
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
	
	public MarginProperties getLeftMarginParent() {
		return leftParent;
	}
	
	public MarginProperties getRightMarginParent() {
		return rightParent;
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
					MarginProperties ret = new MarginProperties(leftMargin.getContent()+StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()), leftMargin.isSpaceOnly());
					r.setLeftMargin(ret);
					r.setRightMargin(rightMargin);
					r.setAlignment(rdp.getAlignment());
					r.setRowSpacing(rdp.getRowSpacing());
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
	
	public int countPreContentRows() {
		return preContentRows.size();
	}
	
	public Iterable<RowImpl> getPreContentRows() {
		return preContentRows;
	}
	
	public int countPostContentRows() {
		return postContentRows.size();
	}
	
	public Iterable<RowImpl> getPostContentRows() {
		return postContentRows;
	}
	
	public int countSkippablePostContentRows() {
		return skippablePostContentRows.size();
	}
	
	public Iterable<RowImpl> getSkippablePostContentRows() {
		return skippablePostContentRows;
	}

	public int getRowCount() {
		return rows.size();
	}

	public Iterator<RowImpl> iterator() {
		return rows.iterator();
	}

	/**
	 * Returns true if this RowDataManager contains objects that makes the formatting volatile,
	 * i.e. prone to change due to for example cross references.
	 * @return returns true if, and only if, the RowDataManager should be discarded if a new pass is requested,
	 * false otherwise
	 */
	public boolean isVolatile() {
		return isVolatile;
	}
	
	private void layout(CharSequence c, String locale) {
		BrailleTranslatorResult btr = getTranslatedResult(c, locale);
		layout(btr);
	}

	private void layout(BrailleTranslatorResult btr) {
		// process first row, is it a new block or should we continue the current row?
		if (rows.size()==0) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listLabel = fcontext.getTranslator().translate(item.getLabel()).getTranslatedRemainder();
				if (item.getType()==FormattingTypes.ListStyle.PL) {
					newRow(btr, listLabel, 0, rdp.getBlockIndentParent());
				} else {
					newRow(btr, listLabel, rdp.getFirstLineIndent(), rdp.getBlockIndent());
				}
				item = null;
			} else {
				newRow(btr, "", rdp.getFirstLineIndent(), rdp.getBlockIndent());
			}
		} else {
			RowImpl r  = rows.pop();
			newRow("", r, btr, rdp.getBlockIndent());
		}
		while (btr.hasNext()) { //LayoutTools.length(chars.toString())>0
			newRow(btr, "", rdp.getTextIndent(), rdp.getBlockIndent());
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

	private void newRow(BrailleTranslatorResult chars, String contentBefore, int indent, int blockIndent) {
		RowImpl row = new RowImpl();
		row.setLeftMargin(leftMargin);
		row.setRightMargin(rightMargin);
		row.setAlignment(rdp.getAlignment());
		row.setRowSpacing(rdp.getRowSpacing());
		newRow(getPreText(contentBefore, indent, blockIndent), row, chars, blockIndent);
	}
	
	private String getPreText(String contentBefore, int indent, int blockIndent) {
		int thisIndent = indent + blockIndent - StringTools.length(contentBefore);
		//assert thisIndent >= 0;
		return contentBefore + StringTools.fill(fcontext.getSpaceCharacter(), thisIndent).toString();
	}
	
	private void newRow(String pre, RowImpl template, BrailleTranslatorResult btr, int blockIndent) {
		newRow(new RowInfo(pre, template), btr, blockIndent);
	}

	//TODO: check leader functionality
	private void newRow(RowInfo m, BrailleTranslatorResult btr, int blockIndent) {
		// [margin][preContent][preTabText][tab][postTabText] 
		//      preContentPos ^
		String tabSpace = "";
		if (currentLeader!=null) {
			int leaderPos = currentLeader.getPosition().makeAbsolute(available);
			int offset = leaderPos-m.preTabPos;
			int align = getLeaderAlign(currentLeader, btr.countRemaining());
			
			if (m.preTabPos>leaderPos || offset - align < 0) { // if tab position has been passed or if text does not fit within row, try on a new row
				rows.add(m.row);
				{
					RowImpl r = new RowImpl();
					r.setLeftMargin(m.row.getLeftMargin());
					r.setRightMargin(rightMargin);
					r.setAlignment(rdp.getAlignment());
					r.setRowSpacing(rdp.getRowSpacing());
					m = new RowInfo(StringTools.fill(fcontext.getSpaceCharacter(), rdp.getTextIndent()+blockIndent), r);
				}
				//update offset
				offset = leaderPos-m.preTabPos;
			}
			if (offset - align > 0) {
				String leaderPattern = fcontext.getTranslator().translate(currentLeader.getPattern()).getTranslatedRemainder();
				tabSpace = StringTools.fill(leaderPattern, offset - align);
			} else {
				Logger.getLogger(this.getClass().getCanonicalName())
					.fine("Leader position has been passed on an empty row or text does not fit on an empty row, ignoring...");
			}
			// discard leader
			currentLeader = null;
		}

		String next = getNext(m, tabSpace, btr);		
		if ("".equals(next) && "".equals(tabSpace)) {
			m.row.setChars(m.preContent + trailingWsBraillePattern.matcher(m.preTabText).replaceAll(""));
			rows.add(m.row);
		} else {
			m.row.setChars(m.preContent + m.preTabText + tabSpace + next);
			rows.add(m.row);
		}
	}
	
	private String getNext(RowInfo m, String tabSpace, BrailleTranslatorResult btr) {
		int contentLen = StringTools.length(tabSpace) + m.preTabTextLen;
		boolean force = contentLen == 0;
		//don't know if soft hyphens need to be replaced, but we'll keep it for now
		return softHyphenPattern.matcher(btr.nextTranslatedRow(m.maxLenText - contentLen, force)).replaceAll("");
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
