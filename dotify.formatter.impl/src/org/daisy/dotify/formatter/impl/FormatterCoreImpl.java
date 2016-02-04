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
	protected final Stack<BlockProperties> propsContext;
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
		this.propsContext = new Stack<>();
		this.leftMargin = new Margin(Type.LEFT);
		this.rightMargin = new Margin(Type.RIGHT);
		this.listItem = null;
		this.blockIndent = 0;
		this.blockIndentParent = new Stack<>();
		blockIndentParent.add(0);
		this.discardIdentifiers = discardIdentifiers;
	}

	@Override
	public void startBlock(BlockProperties p) {
		startBlock(p, null);
	}

	@Override
	public void startBlock(BlockProperties p, String blockId) {
		String lb = "";
		String rb = "";
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			lb = t.getLeftBorder();
			rb = t.getRightBorder();
		}
		leftMargin.push(new MarginComponent(lb, p.getMargin().getLeftSpacing(), p.getPadding().getLeftSpacing()));
		rightMargin.push(new MarginComponent(rb, p.getMargin().getRightSpacing(), p.getPadding().getRightSpacing()));
		if (propsContext.size()>0) {
			addToBlockIndent(propsContext.peek().getBlockIndent());
		}

		RowDataProperties.Builder rdp = new RowDataProperties.Builder().
					textIndent(p.getTextBlockProperties().getTextIndent()).
					firstLineIndent(p.getTextBlockProperties().getFirstLineIndent()).
					align(p.getTextBlockProperties().getAlignment()).
					rowSpacing(p.getTextBlockProperties().getRowSpacing()).
					orphans(p.getOrphans()).
					widows(p.getWidows()).
					blockIndent(blockIndent).
					blockIndentParent(blockIndentParent.peek()).
					leftMargin((Margin)leftMargin.clone()).
					rightMargin((Margin)rightMargin.clone()).
					outerSpaceBefore(p.getMargin().getTopSpacing());
		Block c = newBlock(blockId, rdp.build());
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
		c.setBreakBeforeType(p.getBreakBeforeType());
		c.setKeepType(p.getKeepType());
		c.setKeepWithNext(p.getKeepWithNext());
		if (!discardIdentifiers) {
			c.setIdentifier(p.getTextBlockProperties().getIdentifier());
		}
		c.setKeepWithNextSheets(p.getKeepWithNextSheets());
		c.setVerticalPosition(p.getVerticalPosition());
		propsContext.push(p);
		Block bi = getCurrentBlock();
		RowDataProperties.Builder builder = new RowDataProperties.Builder(bi.getRowDataProperties());
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			if (t.getTopLeftCorner().length()+t.getTopBorder().length()+t.getTopRightCorner().length()>0) {
				builder.leadingDecoration(new SingleLineDecoration(t.getTopLeftCorner(), t.getTopBorder(), t.getTopRightCorner()));
			}
		}
		builder.innerSpaceBefore(p.getPadding().getTopSpacing());
		bi.setRowDataProperties(builder.build());
		//firstRow = true;
	}

	@Override
	public void endBlock() {
		if (listItem!=null) {
			addChars("", new TextProperties.Builder(null).build());
		}
		{
		BlockProperties p = propsContext.pop();
		Block bi = getCurrentBlock();
		RowDataProperties.Builder builder = new RowDataProperties.Builder(bi.getRowDataProperties());
		if (p.getTextBorderStyle()!=null) {
			TextBorderStyle t = p.getTextBorderStyle();
			if (t.getBottomLeftCorner().length()+ t.getBottomBorder().length()+ t.getBottomRightCorner().length()>0) {
				builder.trailingDecoration(new SingleLineDecoration(t.getBottomLeftCorner(), t.getBottomBorder(), t.getBottomRightCorner()));
			}
		}
		builder.innerSpaceAfter(p.getPadding().getBottomSpacing()).
			outerSpaceAfter(bi.getRowDataProperties().getOuterSpaceAfter()+p.getMargin().getBottomSpacing());
		bi.setKeepWithPreviousSheets(p.getKeepWithPreviousSheets());
		bi.setRowDataProperties(builder.build());
		}
		leftMargin.pop();
		rightMargin.pop();
		if (propsContext.size()>0) {
			BlockProperties p = propsContext.peek();
			Keep keep = p.getKeepType();
			int next = p.getKeepWithNext();
			subtractFromBlockIndent(p.getBlockIndent());
			RowDataProperties.Builder rdp = new RowDataProperties.Builder().
						textIndent(p.getTextBlockProperties().getTextIndent()).
						firstLineIndent(p.getTextBlockProperties().getFirstLineIndent()).
						align(p.getTextBlockProperties().getAlignment()).
						rowSpacing(p.getTextBlockProperties().getRowSpacing()).
						orphans(p.getOrphans()).
						widows(p.getWidows()).
						blockIndent(blockIndent).
						blockIndentParent(blockIndentParent.peek()).
						leftMargin((Margin)leftMargin.clone()). //.stackMarginComp(formatterContext, false, false)
						//leftMarginParent((Margin)leftMargin.clone()). //.stackMarginComp(formatterContext, true, false)
						rightMargin((Margin)rightMargin.clone())//. //.stackMarginComp(formatterContext, false, true)
						//rightMarginParent((Margin)rightMargin.clone())
						; //.stackMarginComp(formatterContext, true, true)
			Block c = newBlock(null, rdp.build());
			c.setKeepType(keep);
			c.setKeepWithNext(next);
		}
		//firstRow = true;
	}
	
	public Block newBlock(String blockId, RowDataProperties rdp) {
		return this.push(new RegularBlock(blockId, rdp));
	}
	
	public Block getCurrentBlock() {
		return this.peek();
	}

	@Override
	public void insertMarker(Marker m) {
		//FIXME: this does not work
		getCurrentBlock().addSegment(new MarkerSegment(m));
	}
	
	@Override
	public void insertAnchor(String ref) {
		getCurrentBlock().addSegment(new AnchorSegment(ref));
	}

	@Override
	public void insertLeader(Leader leader) {
		getCurrentBlock().addSegment(new LeaderSegment(leader));
	}

	@Override
	public void addChars(CharSequence c, TextProperties p) {
		Block bl = getCurrentBlock();
		if (listItem!=null) {
			//append to this block
			RowDataProperties.Builder builder = new RowDataProperties.Builder(bl.getRowDataProperties());
			builder.listProperties(new ListItem(listItem.getLabel(), listItem.getType()));
			bl.setRowDataProperties(builder.build());
			//list item has been used now, discard
			listItem = null;
		}
		bl.addSegment(new TextSegment(c.toString(), p));
	}

	@Override
	public void newLine() {
		getCurrentBlock().addSegment(new NewLineSegment());
	}

	@Override
	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		getCurrentBlock().addSegment(new PageNumberReferenceSegment(identifier, numeralStyle));
	}

	@Override
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
