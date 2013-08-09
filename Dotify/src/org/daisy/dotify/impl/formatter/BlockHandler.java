package org.daisy.dotify.impl.formatter;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.BlockProperties;
import org.daisy.dotify.formatter.FormattingTypes;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Leader;
import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.tools.StringTools;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorResult;


/**
 * BlockHandler is responsible for breaking blocks of text into rows. BlockProperties
 * such as list numbers, leaders and margins are resolved in the process. The input
 * text is filtered using the supplied StringFilter before breaking into rows, since
 * the length of the text could change.
 * 
 * @author Joel Håkansson
 */
class BlockHandler {
	private final BrailleTranslator translator;
	private final String spaceChar;
	//private int currentListNumber;
	//private BlockProperties.ListType currentListType;
	private Leader currentLeader;
	private ArrayList<Row> ret;
	private BlockProperties p;
	private final int available;
	private final int rightMargin;
	private ListItem item;
	
	public static class ListItem {
		private String label;
		private FormattingTypes.ListStyle type;
		
		public ListItem(String label, FormattingTypes.ListStyle type) {
			this.label = label;
			this.type = type;
		}
		
		public String getLabel() {
			return label;
		}
		
		public FormattingTypes.ListStyle getType() {
			return type;
		}
	}
	
	public static class Builder {
		private final BrailleTranslator translator;
		private final int available;
		private final int rightMargin;
		
		public Builder(BrailleTranslator translator, LayoutMaster master, int width, int rightMargin) {
			this.translator = translator;
			this.available = width;
			this.rightMargin = rightMargin;
		}
		
		public BlockHandler build() {
			return new BlockHandler(this);
		}
	}
	
	private BlockHandler(Builder builder) {
		this.translator = builder.translator;
		this.currentLeader = null;
		//this.currentListType = BlockProperties.ListType.NONE;
		//this.currentListNumber = 0;
		this.ret = new ArrayList<Row>();
		this.p = new BlockProperties.Builder().build();
		this.available = builder.available;
		this.rightMargin = builder.rightMargin;
		this.item = null;
		this.spaceChar = translator.translate(" ").getTranslatedRemainder();
		
	}
	/*
	public void setCurrentListType(BlockProperties.ListType type) {
		currentListType = type;
	}
	
	public BlockProperties.ListType getCurrentListType() {
		return currentListType;
	}
	
	public void setCurrentListNumber(int value) {
		currentListNumber = value;
	}
	
	public int getCurrentListNumber() {
		return currentListNumber;
	}
	*/
	
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
	 * Gets the current list item.
	 * @return returns the current list item, or null if there is no current list item
	 */
	public ListItem getListItem() {
		return item;
	}

	public void setBlockProperties(BlockProperties p) {
		this.p = p;
	}
	
	public BlockProperties getBlockProperties() {
		return p;
	}
	/*
	public void setWidth(int value) {
		available = value;
	}*/
	
	public int getWidth() {
		return available;
	}
	
	public void setCurrentLeader(Leader l) {
		currentLeader = l;
	}
	
	public Leader getCurrentLeader() {
		return currentLeader;
	}
	/*
	public void setBlockIndent(int value) {
		this.blockIndent = value;
	}*/
	
	/**
	 * Break text into rows. 
	 * @param btr the translator result to break into rows
	 * @param leftMargin left margin of the text
	 * @param blockIndent the block indent
	 * @param blockIndentParent the block indent parent
	 * @return returns an ArrayList of Rows
	 */
	public ArrayList<Row> layoutBlock(BrailleTranslatorResult btr, int leftMargin, int blockIndent, int blockIndentParent) {
		return layoutBlock(btr, leftMargin, null, blockIndent, blockIndentParent);
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
	public ArrayList<Row> appendBlock(BrailleTranslatorResult btr, int leftMargin, Row row, int blockIndent, int blockIndentParent) {
		return layoutBlock(btr, leftMargin, row, blockIndent, blockIndentParent);
	}

	private ArrayList<Row> layoutBlock(BrailleTranslatorResult btr, int leftMargin, Row r, int blockIndent, int blockIndentParent) {
		ret = new ArrayList<Row>();
		// process first row, is it a new block or should we continue the current row?
		if (r==null) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listLabel = translator.translate(item.getLabel()).getTranslatedRemainder();
				if (item.getType()==FormattingTypes.ListStyle.PL) {
					int bypassBlockIndent = blockIndent;
					blockIndent = blockIndentParent;
					newRow(listLabel, btr, available, leftMargin, 0, p, blockIndent);
					blockIndent = bypassBlockIndent;
				} else {
					newRow(listLabel, btr, available, leftMargin, p.getFirstLineIndent(), p, blockIndent);
				}
				item = null;
			} else {
				newRow("", btr, available, leftMargin, p.getFirstLineIndent(), p, blockIndent);
			}
		} else {
			newRow(r.getMarkers(), r.getLeftMargin(), "", r.getChars().toString(), btr, available, p, blockIndent);
		}
		while (btr.hasNext()) { //LayoutTools.length(chars.toString())>0
			newRow("", btr, available, leftMargin, p.getTextIndent(), p, blockIndent);
		}
		return ret;
	}

	private void newRow(String contentBefore, BrailleTranslatorResult chars, int available, int margin, int indent, BlockProperties p, int blockIndent) {
		int thisIndent = indent + blockIndent - StringTools.length(contentBefore);
		//assert thisIndent >= 0;
		String preText = contentBefore + StringTools.fill(spaceChar, thisIndent).toString();
		newRow(null, margin, preText, "", chars, available, p, blockIndent);
	}

	//TODO: check leader functionality
	private void newRow(List<Marker> r, int margin, String preContent, String preTabText, BrailleTranslatorResult btr, int available, BlockProperties p, int blockIndent) {

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

		int width = available;
		String tabSpace = "";
		if (currentLeader!=null) {
			int leaderPos = currentLeader.getPosition().makeAbsolute(width);
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
				Row row = new Row(preContent + preTabText);
				row.setLeftMargin(margin);
				row.setRightMargin(rightMargin);
				row.setAlignment(p.getAlignment());
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
				String leaderPattern = translator.translate(currentLeader.getPattern()).getTranslatedRemainder();
				tabSpace = StringTools.fill(leaderPattern, offset - align);
			} // else: leader position has been passed on an empty row or text does not fit on an empty row, ignore
		}

		maxLenText -= StringTools.length(tabSpace);
		maxLenText -= StringTools.length(preTabText);

		boolean force = maxLenText >= available - (preContentPos);
		String next = btr.nextTranslatedRow(maxLenText, force);
		Row nr;
		if ("".equals(next) && "".equals(tabSpace)) {
			nr = new Row(preContent + preTabText.replaceAll("[\\s\u2800]+\\z", ""));
		} else {
			nr = new Row(preContent + preTabText + tabSpace + next);
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
		/*
		if (nr.getChars().length()>master.getFlowWidth()) {
			throw new RuntimeException("Row is too long (" + nr.getChars().length() + "/" + master.getFlowWidth() + ") '" + nr.getChars() + "'");
		}*/
		ret.add(nr);
	}
}
