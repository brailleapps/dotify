package org.daisy.dotify.formatter.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.formatter.BlockProperties;
import org.daisy.dotify.formatter.FormattingTypes;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.formatter.Leader;
import org.daisy.dotify.formatter.Marker;
import org.daisy.dotify.formatter.Row;
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
	private int blockIndent;
	private Stack<Integer> blockIndentParent;
	
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
	
	public BlockHandler(StringFilter filters) {
		this.filters = filters;
		this.currentLeader = null;
		//this.currentListType = BlockProperties.ListType.NONE;
		//this.currentListNumber = 0;
		this.ret = new ArrayList<Row>();
		this.p = new BlockProperties.Builder().build();
		this.available = 0;
		this.item = null;
		this.blockIndent = 0;
		this.blockIndentParent = new Stack<Integer>();
		blockIndentParent.add(0);
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
	
	public void addToBlockIndent(int value) {
		blockIndentParent.push(blockIndent);
		blockIndent += value;
	}
	
	public void subtractFromBlockIndent(int value) {
		int test = blockIndentParent.pop();
		blockIndent -= value;
		assert blockIndent==test;
	}
	
	/**
	 * Break text into rows. 
	 * @param text the text to break into rows
	 * @param leftMargin left margin of the text
	 * @param master the layout master to use
	 * @return returns an ArrayList of Rows
	 */
	public ArrayList<Row> layoutBlock(CharSequence text, int leftMargin, LayoutMaster master) {
		return layoutBlock(text, leftMargin, true, null, master);
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
	public ArrayList<Row> appendBlock(CharSequence text, int leftMargin, Row row, LayoutMaster master) {
		return layoutBlock(text, leftMargin, false, row, master);
	}

	private ArrayList<Row> layoutBlock(CharSequence c, int leftMargin, boolean firstRow, Row r, LayoutMaster master) {
		ret = new ArrayList<Row>();
		String chars = filters.filter(c.toString());
		// process first row, is it a new block or should we continue the current row?
		if (firstRow) {
			// add to left margin
			if (item!=null) { //currentListType!=BlockProperties.ListType.NONE) {
				String listLabel = filters.filter(item.getLabel());
				if (item.getType()==FormattingTypes.ListStyle.PL) {
					int bypassBlockIndent = blockIndent;
					blockIndent = blockIndentParent.peek();
					chars = newRow(listLabel, chars, available, leftMargin, 0, master, p);
					blockIndent = bypassBlockIndent;
				} else {
					chars = newRow(listLabel, chars, available, leftMargin, p.getFirstLineIndent(), master, p);
				}
				item = null;
			} else {
				chars = newRow("", chars, available, leftMargin, p.getFirstLineIndent(), master , p);
			}
		} else {
			chars = newRow(r.getMarkers(), r.getLeftMargin(), "", r.getChars().toString(), chars, available, master, p);
		}
		while (LayoutTools.length(chars.toString())>0) {
			String c2 = newRow("", chars, available, leftMargin, p.getTextIndent(), master, p);
			//c2 = c2.replaceFirst("\\A\\s*", ""); // remove leading white space from input
			if (c2.length()>=chars.length()) {
				System.out.println(c2);
			}
			chars = c2;
		}
		return ret;
	}

	private String newRow(String contentBefore, String chars, int available, int margin, int indent, LayoutMaster master, BlockProperties p) {
		int thisIndent = indent + blockIndent - LayoutTools.length(contentBefore);
		//assert thisIndent >= 0;
		String preText = contentBefore + LayoutTools.fill(SPACE_CHAR, thisIndent).toString();
		return newRow(null, margin, preText, "", chars, available, master, p);
	}

	//TODO: check leader functionality
	private String newRow(List<Marker> r, int margin, String preContent, String preTabText, String postTabText, int available, LayoutMaster master, BlockProperties p) {

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
