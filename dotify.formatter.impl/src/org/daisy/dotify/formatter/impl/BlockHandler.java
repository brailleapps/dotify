package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.BlockProperties;
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
class BlockHandler {
	private final String spaceChar;
	private final RowDataProperties rdp;
	private final int available;
	private final int rightMargin;
	
	private Leader currentLeader;
	private ArrayList<RowImpl> ret;
	private BlockProperties p;
	private ListItem item;

	public static class Builder {
		private final RowDataProperties rdp;
		
		public Builder(RowDataProperties rdp) {
			this.rdp = rdp;
		}
		
		public BlockHandler build() {
			return new BlockHandler(this);
		}
	}
	
	private BlockHandler(Builder builder) {
		this.currentLeader = null;
		this.ret = new ArrayList<RowImpl>();
		this.p = new BlockProperties.Builder().build();
		this.rdp = builder.rdp;
		this.available = rdp.getMaster().getFlowWidth() - rdp.getRightMargin();
		this.rightMargin = rdp.getRightMargin();
		this.item = null;
		this.spaceChar = rdp.getTranslator().translate(" ").getTranslatedRemainder();
		
	}
	
	//TODO: if list type is only used to differentiate between pre and other lists, and pre implies that label.equals(""), then type could be removed
	/**
	 * Sets the list item to use for the following call to layoutBlock. Since
	 * the list item label is resolved prior to this call, the list type
	 * is only used to differentiate between pre formatted list items and other
	 * types of lists. 
	 * @param label the resolved list item label, typically a number or a bullet 
	 * @param type type of list item
	 */
	public void setListItem(String label, FormattingTypes.ListStyle type) {
		item = new ListItem(label, type);
	}
	
	/**
	 * Sets the list item to use for the following call to layoutBlock / appendBlock.
	 * @param item, the list item
	 */
	public void setListItem(ListItem item) {
		this.item = item;
	}
	
	/**
	 * Gets the current list item.
	 * @return returns the current list item, or null if there is no current list item
	 */
	public ListItem getListItem() {
		return item;
	}

	public void setBlockProperties(BlockProperties p) {
		this.p = p;
	}
	
	public int getWidth() {
		return available;
	}
	
	public void setCurrentLeader(Leader l) {
		currentLeader = l;
	}
	
	public Leader getCurrentLeader() {
		return currentLeader;
	}
	
	/**
	 * Break text into rows. 
	 * @param btr the translator result to break into rows
	 * @param leftMargin left margin of the text
	 * @param blockIndent the block indent
	 * @param blockIndentParent the block indent parent
	 * @return returns an ArrayList of Rows
	 */
	public ArrayList<RowImpl> layoutBlock(CharSequence c, String locale) {
		return layoutBlock(c, null, locale);
	}
	
	/**
	 * Continue a block of text, starting on the supplied row.
	 * @param btr the translator result to break into rows
	 * @param leftMargin left margin of the text
	 * @param row the row to continue the layout on
	 * @param blockIndent the block indent
	 * @param blockIndentParent the block indent parent
	 * @return returns an ArrayList of Rows. The first row being the supplied row, with zero or more characters
	 * from <tt>text</tt>
	 */
	public ArrayList<RowImpl> appendBlock(CharSequence c, RowImpl r, String locale) {
		return layoutBlock(c, r, locale);
	}

	private ArrayList<RowImpl> layoutBlock(CharSequence c, RowImpl r, String locale) {
		BrailleTranslatorResult btr = getTranslatedResult(c, locale);
		ret = new ArrayList<RowImpl>();
		// process first row, is it a new block or should we continue the current row?
		if (r==null) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listLabel = rdp.getTranslator().translate(item.getLabel()).getTranslatedRemainder();
				if (item.getType()==FormattingTypes.ListStyle.PL) {
					newRow(listLabel, btr, rdp.getLeftMargin(), 0, rdp.getBlockIndentParent());
				} else {
					newRow(listLabel, btr, rdp.getLeftMargin(), p.getFirstLineIndent(), rdp.getBlockIndent());
				}
				item = null;
			} else {
				newRow("", btr, rdp.getLeftMargin(), p.getFirstLineIndent(), rdp.getBlockIndent());
			}
		} else {
			newRow(r.getMarkers(), r.getLeftMargin(), "", r.getChars().toString(), btr, rdp.getBlockIndent());
		}
		while (btr.hasNext()) { //LayoutTools.length(chars.toString())>0
			newRow("", btr, rdp.getLeftMargin(), p.getTextIndent(), rdp.getBlockIndent());
		}
		return ret;
	}
	
	private BrailleTranslatorResult getTranslatedResult(CharSequence c, String locale) {
		BrailleTranslatorResult btr;
		if (locale!=null) {
			try {
				btr = rdp.getTranslator().translate(c.toString(), locale);
			} catch (TranslationException e) {
				Logger.getLogger(this.getClass().getCanonicalName())
					.log(Level.WARNING, "Failed to translate using the specified locale: " + locale + ". Using default", e);
				btr = rdp.getTranslator().translate(c.toString());
			}
		} else {
			btr = rdp.getTranslator().translate(c.toString());
		}
		return btr;
	}

	private void newRow(String contentBefore, BrailleTranslatorResult chars, int margin, int indent, int blockIndent) {
		int thisIndent = indent + blockIndent - StringTools.length(contentBefore);
		//assert thisIndent >= 0;
		String preText = contentBefore + StringTools.fill(spaceChar, thisIndent).toString();
		newRow(null, margin, preText, "", chars, blockIndent);
	}

	//TODO: check leader functionality
	private void newRow(List<Marker> r, int margin, String preContent, String preTabText, BrailleTranslatorResult btr, int blockIndent) {

		// [margin][preContent][preTabText][tab][postTabText] 
		//      preContentPos ^

		int preTextIndent = StringTools.length(preContent);
		int preContentPos = margin+preTextIndent;
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
				row.setAlignment(p.getAlignment());
				row.setRowSpacing(p.getRowSpacing());
				if (r!=null) {
					row.addMarkers(r);
					r = null;
				}
				ret.add(row);

				preContent = StringTools.fill(spaceChar, p.getTextIndent()+blockIndent);
				preTextIndent = StringTools.length(preContent);
				preTabText = "";
				
				preContentPos = margin+preTextIndent;
				preTabPos = preContentPos;
				maxLenText = available-(preContentPos);
				offset = leaderPos-preTabPos;
			}
			if (offset - align > 0) {
				String leaderPattern = rdp.getTranslator().translate(currentLeader.getPattern()).getTranslatedRemainder();
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
		nr.setAlignment(p.getAlignment());
		nr.setRowSpacing(p.getRowSpacing());
		/*
		if (nr.getChars().length()>master.getFlowWidth()) {
			throw new RuntimeException("Row is too long (" + nr.getChars().length() + "/" + master.getFlowWidth() + ") '" + nr.getChars() + "'");
		}*/
		ret.add(nr);
	}
}
