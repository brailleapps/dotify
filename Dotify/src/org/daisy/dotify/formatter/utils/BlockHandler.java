package org.daisy.dotify.formatter.utils;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.formatter.dom.BlockProperties;
import org.daisy.dotify.formatter.dom.FormattingTypes;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Leader;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.Row;
import org.daisy.dotify.text.StringFilter;


/**
 * BlockHandler is responsible for breaking blocks of text into rows. BlockProperties
 * such as list numbers, leaders and margins are resolved in the process. The input
 * text is filtered using the supplied StringFilter before breaking into rows, since
 * the length of the text could change.
 * 
 * @author Joel Håkansson, TPB
 */
public class BlockHandler {
	private static final Character SPACE_CHAR = ' ';
	private final StringFilter filters;
	//private int currentListNumber;
	//private BlockProperties.ListType currentListType;
	private Leader currentLeader;
	private ArrayList<Row> ret;
	private BlockProperties p;
	private int available;
	private ListItem item;
	private final LayoutMaster master;
	
	public class ListItem {
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
		private final StringFilter filters;
		private final LayoutMaster master;
		
		public Builder(StringFilter filters, LayoutMaster master) {
			this.filters = filters;
			this.master = master;
		}
		
		public BlockHandler build() {
			return new BlockHandler(filters, master);
		}
	}
	
	private BlockHandler(StringFilter filters, LayoutMaster master) {
		this.filters = filters;
		this.currentLeader = null;
		//this.currentListType = BlockProperties.ListType.NONE;
		//this.currentListNumber = 0;
		this.ret = new ArrayList<Row>();
		this.p = new BlockProperties.Builder().build();
		this.available = 0;
		this.item = null;
		this.master = master;
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
	
	public void setWidth(int value) {
		available = value;
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
	/*
	public void setBlockIndent(int value) {
		this.blockIndent = value;
	}*/
	
	/**
	 * Break text into rows. 
	 * @param text the text to break into rows
	 * @param leftMargin left margin of the text
	 * @param master the layout master to use
	 * @return returns an ArrayList of Rows
	 */
	public ArrayList<Row> layoutBlock(CharSequence text, int leftMargin, int blockIndent, int blockIndentParent) {
		return layoutBlock(text, leftMargin, null, blockIndent, blockIndentParent);
	}
	
	/**
	 * Continue a block of text, starting on the supplied row.
	 * @param text the text to break into rows
	 * @param leftMargin left margin of the text
	 * @param row the row to continue the layout on
	 * @param master the layout master to use
	 * @return returns an ArrayList of Rows. The first row being the supplied row, with zero or more characters
	 * from <tt>text</tt>
	 */
	public ArrayList<Row> appendBlock(CharSequence text, int leftMargin, Row row, int blockIndent, int blockIndentParent) {
		return layoutBlock(text, leftMargin, row, blockIndent, blockIndentParent);
	}

	private ArrayList<Row> layoutBlock(CharSequence c, int leftMargin, Row r, int blockIndent, int blockIndentParent) {
		ret = new ArrayList<Row>();
		String chars = filters.filter(c.toString());
		// process first row, is it a new block or should we continue the current row?
		if (r==null) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listLabel = filters.filter(item.getLabel());
				if (item.getType()==FormattingTypes.ListStyle.PL) {
					int bypassBlockIndent = blockIndent;
					blockIndent = blockIndentParent;
					chars = newRow(listLabel, chars, available, leftMargin, 0, p, blockIndent);
					blockIndent = bypassBlockIndent;
				} else {
					chars = newRow(listLabel, chars, available, leftMargin, p.getFirstLineIndent(), p, blockIndent);
				}
				item = null;
			} else {
				chars = newRow("", chars, available, leftMargin, p.getFirstLineIndent(), p, blockIndent);
			}
		} else {
			chars = newRow(r.getMarkers(), r.getLeftMargin(), "", r.getChars().toString(), chars, available, p, blockIndent);
		}
		while (LayoutTools.length(chars.toString())>0) {
			String c2 = newRow("", chars, available, leftMargin, p.getTextIndent(), p, blockIndent);
			//c2 = c2.replaceFirst("\\A\\s*", ""); // remove leading white space from input
			if (c2.length()>=chars.length()) {
				System.out.println(c2);
			}
			chars = c2;
		}
		return ret;
	}

	private String newRow(String contentBefore, String chars, int available, int margin, int indent, BlockProperties p, int blockIndent) {
		int thisIndent = indent + blockIndent - LayoutTools.length(contentBefore);
		//assert thisIndent >= 0;
		String preText = contentBefore + LayoutTools.fill(SPACE_CHAR, thisIndent).toString();
		return newRow(null, margin, preText, "", chars, available, p, blockIndent);
	}

	//TODO: check leader functionality
	private String newRow(List<Marker> r, int margin, String preContent, String preTabText, String postTabText, int available, BlockProperties p, int blockIndent) {

		// [margin][preContent][preTabText][tab][postTabText] 
		//      preContentPos ^

		int preTextIndent = LayoutTools.length(preContent);
		int preContentPos = margin+preTextIndent;
		int preTabPos = preContentPos+LayoutTools.length(preTabText.replaceAll("\u00ad", ""));
		int postTabTextLen = LayoutTools.length(postTabText.replaceAll("\u00ad", ""));
		int maxLenText = available-(preContentPos);
		if (maxLenText<1) {
			throw new RuntimeException("Cannot continue layout: No space left for characters.");
		}

		int width = master.getFlowWidth();
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
				if (r!=null) {
					row.addMarkers(r);
					r = null;
				}
				ret.add(row);

				preContent = LayoutTools.fill(SPACE_CHAR, p.getTextIndent()+blockIndent);
				preTextIndent = LayoutTools.length(preContent);
				preTabText = "";
				preContentPos = margin+preTextIndent;
				preTabPos = preContentPos;
				maxLenText = available-(preContentPos);
				offset = leaderPos-preTabPos;
			}
			if (offset - align > 0) {
				String leaderPattern = filters.filter(currentLeader.getPattern());
				tabSpace = LayoutTools.fill(leaderPattern, offset - align);
			} // else: leader position has been passed on an empty row or text does not fit on an empty row, ignore
		}

		maxLenText -= LayoutTools.length(tabSpace);

		BreakPoint bp = null;
		Row nr = null;
		
		if (tabSpace.length()>0) { // there is a tab...
			maxLenText -= preTabText.length();
			BreakPointHandler bph = new BreakPointHandler(postTabText);
			bp = bph.nextRow(maxLenText);
			nr = new Row(preContent + preTabText + tabSpace + bp.getHead());
		} else { // no tab
			BreakPointHandler bph = new BreakPointHandler(preTabText + postTabText);
			bp = bph.nextRow(maxLenText);
			nr = new Row(preContent + bp.getHead());
		}
		// discard leader
		currentLeader = null;

		assert nr != null;
		if (r!=null) {
			nr.addMarkers(r);
		}
		nr.setLeftMargin(margin);

		ret.add(nr);
		return bp.getTail();
	}
}
