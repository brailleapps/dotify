package org.daisy.dotify.formatter.impl;

import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.FormattingTypes.Keep;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.tools.StringTools;

class FormatterCoreImpl extends BlockSequenceImpl implements FormatterCore {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7775469339792146048L;
	private final BrailleTranslator translator;
	private final Stack<BlockProperties> context;
	private final char marginChar;
	private Stack<String> leftMargin;
	private Stack<String> rightMargin;
	
	private Stack<Integer> blockIndentParent;
	private int blockIndent;
	private ListItem listItem;

	// TODO: fix recursive keep problem
	// TODO: Implement floating elements
	public FormatterCoreImpl(SequenceProperties p, LayoutMaster master, BrailleTranslator translator) {
		super(p, master);
		this.translator = translator;
		this.context = new Stack<BlockProperties>();
		this.leftMargin = new Stack<String>();
		this.rightMargin = new Stack<String>();
		this.listItem = null;
		//margin char can only be a single character, the reason for going through the translator
		//is because output isn't always braille.
		this.marginChar = translator.translate(" ").getTranslatedRemainder().charAt(0);
		this.blockIndent = 0;
		this.blockIndentParent = new Stack<Integer>();
		blockIndentParent.add(0);
	}

	public void startBlock(BlockProperties p) {
		startBlock(p, null);
	}

	public void startBlock(BlockProperties p, String blockId) {
		String lb = "";
		String rb = "";
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			lb = t.getLeftBorder();
			rb = t.getRightBorder();
		}
		leftMargin.push(StringTools.fill(marginChar, p.getLeftMargin()));
		leftMargin.push(lb);
		rightMargin.push(StringTools.fill(marginChar, p.getRightMargin()));
		rightMargin.push(rb);
		if (context.size()>0) {
			addToBlockIndent(context.peek().getBlockIndent());
		}
		RowDataProperties.Builder rdp = new RowDataProperties.Builder(
				translator, getLayoutMaster()).
					blockIndent(blockIndent).
					blockIndentParent(blockIndentParent.peek()).
					leftMargin(stackString(leftMargin, false)).
					leftMarginParent(stackString(leftMargin.subList(0, leftMargin.size()-1), false)).
					rightMargin(stackString(rightMargin, true)).
					rightMarginParent(stackString(rightMargin.subList(0, rightMargin.size()-1), true));
		BlockImpl c = newBlock(blockId, rdp);
		if (context.size()>0) {
			if (context.peek().getListType()!=FormattingTypes.ListStyle.NONE) {
				String listLabel;
				switch (context.peek().getListType()) {
				case OL:
					listLabel = context.peek().nextListNumber()+""; break;
				case UL:
					listLabel = "•";
					break;
				case PL: default:
					listLabel = "";
				}
				listItem = new ListItem(listLabel, context.peek().getListType());
			}
		}
		c.addSpaceBefore(p.getTopMargin());
		c.setBreakBeforeType(p.getBreakBeforeType());
		c.setKeepType(p.getKeepType());
		c.setKeepWithNext(p.getKeepWithNext());
		c.setIdentifier(p.getIdentifier());
		c.setKeepWithNextSheets(p.getKeepWithNextSheets());
		c.setVerticalPosition(p.getVerticalPosition());
		context.push(p);
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			BlockImpl bi = getCurrentBlock();
			if (t.getTopLeftCorner().length()+t.getTopBorder().length()+t.getTopRightCorner().length()>0) {
				bi.setLeadingDecoration(new SingleLineDecoration(t.getTopLeftCorner(), t.getTopBorder(), t.getTopRightCorner()));
			}
		}
		//firstRow = true;
	}

	public void endBlock() {
		if (listItem!=null) {
			addChars("", new TextProperties.Builder(null).build());
		}
		BlockProperties p = context.pop();
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			if (t.getBottomLeftCorner().length()+ t.getBottomBorder().length()+ t.getBottomRightCorner().length()>0) {
				getCurrentBlock()
				.setTrailingDecoration(new SingleLineDecoration(t.getBottomLeftCorner(), t.getBottomBorder(), t.getBottomRightCorner()));
			}
		}
		getCurrentBlock().addSpaceAfter(p.getBottomMargin());
		getCurrentBlock().setKeepWithPreviousSheets(p.getKeepWithPreviousSheets());
		leftMargin.pop();
		leftMargin.pop();
		rightMargin.pop();
		rightMargin.pop();
		if (context.size()>0) {
			Keep keep = context.peek().getKeepType();
			int next = context.peek().getKeepWithNext();
			subtractFromBlockIndent(context.peek().getBlockIndent());
			RowDataProperties.Builder rdp = new RowDataProperties.Builder(
					translator, getLayoutMaster()).
						blockIndent(blockIndent).
						blockIndentParent(blockIndentParent.peek()).
						leftMargin(stackString(leftMargin, false)).
						leftMarginParent(stackString(leftMargin.subList(0, leftMargin.size()-1), false)).
						rightMargin(stackString(rightMargin, true)).
						rightMarginParent(stackString(rightMargin.subList(0, rightMargin.size()-1), true));
			BlockImpl c = newBlock(null, rdp);
			c.setKeepType(keep);
			c.setKeepWithNext(next);
		}
		//firstRow = true;
	}

	public void startFloat(String id) {
		// TODO implement float
		throw new UnsupportedOperationException("Not implemented");
	}

	public void endFloat() {
		// TODO implement float
		throw new UnsupportedOperationException("Not implemented");
	}

	public void insertMarker(Marker m) {
		//FIXME: this does not work
		getCurrentBlock().addMarker(m);
	}

	public void insertAnchor(String ref) {
		// TODO implement anchor
		throw new UnsupportedOperationException("Not implemented");
	}

	public void insertLeader(Leader leader) {
		getCurrentBlock().insertLeader(leader);
	}

	public void addChars(CharSequence c, TextProperties p) {
		assert context.size()!=0;
		if (context.size()==0) return;
		BlockImpl bl = getCurrentBlock();
		if (listItem!=null) {
			//append to this block
			bl.setListItem(listItem.getLabel(), listItem.getType());
			//list item has been used now, discard
			listItem = null;
		}
		bl.addChars(c, p, context.peek());		
	}

	public void newLine() {
		MarginProperties p = stackString(leftMargin, false);
		MarginProperties ret = new MarginProperties(p.getContent()+StringTools.fill(marginChar, context.peek().getTextIndent()), p.isSpaceOnly());
		getCurrentBlock().newLine(ret);
	}

	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		getCurrentBlock().insertReference(identifier, numeralStyle);
	}

	public void insertEvaluate(String exp, TextProperties t) {
		throw new UnsupportedOperationException("Not implemented");
	}
	
	private void addToBlockIndent(int value) {
		blockIndentParent.push(blockIndent);
		blockIndent += value;
	}
	
	private void subtractFromBlockIndent(int value) {
		int test = blockIndentParent.pop();
		blockIndent -= value;
		assert blockIndent==test;
	}
	
	private MarginProperties stackString(List<String> inp, boolean reverse) {
		StringBuilder sb = new StringBuilder();
		if (reverse) {
			for (int i = inp.size()-1; i>=0; i--) {
				sb.append(inp.get(i));
			}
		} else {
			for (String s : inp) {
				sb.append(s);
			}
		}
		//Performance optimization
		boolean isSpace = true;
		for (int i = 0; i<sb.length(); i++) {
			if (sb.charAt(i)!=marginChar) {
				isSpace = false;
				break;
			}
		}
		return new MarginProperties(sb.toString(), isSpace);
	}

}
