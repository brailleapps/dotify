package org.daisy.dotify.formatter.impl;

import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.FormattingTypes.Keep;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.formatter.impl.Margin.Type;

class FormatterCoreImpl extends Stack<Block> implements FormatterCore, BlockGroup {
	/**
	 * 
	 */
	private static final long serialVersionUID = -7775469339792146048L;
	private final Stack<BlockProperties> propsContext;
	private Margin leftMargin;
	private Margin rightMargin;
	
	private Stack<Integer> blockIndentParent;
	private int blockIndent;
	private ListItem listItem;
	
	private final boolean discardIdentifiers;

	// TODO: fix recursive keep problem
	// TODO: Implement floating elements
	public FormatterCoreImpl() {
		this(false);
	}
	
	public FormatterCoreImpl(boolean discardIdentifiers) {
		super();
		this.propsContext = new Stack<BlockProperties>();
		this.leftMargin = new Margin(Type.LEFT);
		this.rightMargin = new Margin(Type.RIGHT);
		this.listItem = null;
		this.blockIndent = 0;
		this.blockIndentParent = new Stack<Integer>();
		blockIndentParent.add(0);
		this.discardIdentifiers = discardIdentifiers;
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
		leftMargin.push(new MarginComponent(lb, p.getLeftMargin()));
		rightMargin.push(new MarginComponent(rb, p.getRightMargin()));
		if (propsContext.size()>0) {
			addToBlockIndent(propsContext.peek().getBlockIndent());
		}
		RowDataProperties.Builder rdp = new RowDataProperties.Builder().
					textIndent(p.getTextIndent()).
					firstLineIndent(p.getFirstLineIndent()).
					align(p.getAlignment()).
					rowSpacing(p.getRowSpacing()).
					
					blockIndent(blockIndent).
					blockIndentParent(blockIndentParent.peek()).
					leftMargin((Margin)leftMargin.clone()). //.stackMarginComp(formatterContext, false, false)
					//leftMarginParent((Margin)leftMargin.clone()). //.stackMarginComp(formatterContext, true, false)
					rightMargin((Margin)rightMargin.clone())//. //.stackMarginComp(formatterContext, false, true)
					//rightMarginParent((Margin)rightMargin.clone())
					; //.stackMarginComp(formatterContext, true, true)
		Block c = newBlock(blockId, rdp);
		if (propsContext.size()>0) {
			if (propsContext.peek().getListType()!=FormattingTypes.ListStyle.NONE) {
				String listLabel;
				switch (propsContext.peek().getListType()) {
				case OL:
					listLabel = propsContext.peek().nextListNumber()+""; break;
				case UL:
					listLabel = "•";
					break;
				case PL: default:
					listLabel = "";
				}
				listItem = new ListItem(listLabel, propsContext.peek().getListType());
			}
		}
		c.addSpaceBefore(p.getTopMargin());
		c.setBreakBeforeType(p.getBreakBeforeType());
		c.setKeepType(p.getKeepType());
		c.setKeepWithNext(p.getKeepWithNext());
		if (!discardIdentifiers) {
			c.setIdentifier(p.getIdentifier());
		}
		c.setKeepWithNextSheets(p.getKeepWithNextSheets());
		c.setVerticalPosition(p.getVerticalPosition());
		propsContext.push(p);
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			Block bi = getCurrentBlock();
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
		BlockProperties p = propsContext.pop();
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
		rightMargin.pop();
		if (propsContext.size()>0) {
			Keep keep = propsContext.peek().getKeepType();
			int next = propsContext.peek().getKeepWithNext();
			subtractFromBlockIndent(propsContext.peek().getBlockIndent());
			RowDataProperties.Builder rdp = new RowDataProperties.Builder().
						textIndent(p.getTextIndent()).
						firstLineIndent(p.getFirstLineIndent()).
						align(p.getAlignment()).
						rowSpacing(p.getRowSpacing()).
						blockIndent(blockIndent).
						blockIndentParent(blockIndentParent.peek()).
						leftMargin((Margin)leftMargin.clone()). //.stackMarginComp(formatterContext, false, false)
						//leftMarginParent((Margin)leftMargin.clone()). //.stackMarginComp(formatterContext, true, false)
						rightMargin((Margin)rightMargin.clone())//. //.stackMarginComp(formatterContext, false, true)
						//rightMarginParent((Margin)rightMargin.clone())
						; //.stackMarginComp(formatterContext, true, true)
			Block c = newBlock(null, rdp);
			c.setKeepType(keep);
			c.setKeepWithNext(next);
		}
		//firstRow = true;
	}
	
	public Block newBlock(String blockId, RowDataProperties.Builder rdp) {
		return this.push(new Block(blockId, rdp));
	}
	
	public Block getCurrentBlock() {
		return this.peek();
	}

	public void insertMarker(Marker m) {
		//FIXME: this does not work
		getCurrentBlock().addSegment(new MarkerSegment(m));
	}
	
	public void insertAnchor(String ref) {
		getCurrentBlock().addSegment(new AnchorSegment(ref));
	}

	public void insertLeader(Leader leader) {
		getCurrentBlock().addSegment(new LeaderSegment(leader));
	}

	public void addChars(CharSequence c, TextProperties p) {
		Block bl = getCurrentBlock();
		if (listItem!=null) {
			//append to this block
			bl.setListItem(listItem.getLabel(), listItem.getType());
			//list item has been used now, discard
			listItem = null;
		}
		bl.addSegment(new TextSegment(c.toString(), p));
	}

	public void newLine() {
		getCurrentBlock().addSegment(new NewLineSegment());
	}

	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		getCurrentBlock().addSegment(new PageNumberReferenceSegment(identifier, numeralStyle));
	}

	public void insertEvaluate(DynamicContent exp, TextProperties t) {
		getCurrentBlock().addSegment(new Evaluate(exp, t));
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

	@Override
	public List<Block> getBlocks(FormatterContext context, DefaultContext c,
			CrossReferences crh) {
		return this;
	}

	@Override
	public boolean isGenerated() {
		return false;
	}
	
}
