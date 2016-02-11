package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.FormatterSequence;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TableProperties;
import org.daisy.dotify.api.translator.Border;
import org.daisy.dotify.api.translator.TextBorderConfigurationException;
import org.daisy.dotify.api.translator.TextBorderFactory;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.formatter.impl.Margin.Type;

/**
 * Provides an interface for a sequence of block contents.
 * 
 * @author Joel Håkansson
 */
class BlockSequence extends FormatterCoreImpl implements FormatterSequence {
	private static final long serialVersionUID = -6105005856680272131L;
	private final FormatterContext fc;
	private final static Logger logger = Logger.getLogger(BlockSequence.class.getCanonicalName());
	private final LayoutMaster master;
	private final Integer initialPagenum;
	private Table table;
	
	public BlockSequence(FormatterContext fc, Integer initialPagenum, LayoutMaster master) {
		this.initialPagenum = initialPagenum;
		this.master = master;
		this.fc = fc;
	}

	/**
	 * Gets the layout master for this sequence
	 * @return returns the layout master for this sequence
	 */
	public LayoutMaster getLayoutMaster() {
		return master;
	}

	/**
	 * Gets the block with the specified index, where index >= 0 && index < getBlockCount()
	 * @param index the block index
	 * @return returns the block index
	 * @throws IndexOutOfBoundsException if index < 0 || index >= getBlockCount()
	 */
	private Block getBlock(int index) {
		return this.elementAt(index);
	}

	/**
	 * Gets the number of blocks in this sequence
	 * @return returns the number of blocks in this sequence
	 */
	private int getBlockCount() {
		return this.size();
	}

	/**
	 * Get the initial page number, i.e. the number that the first page in the sequence should have
	 * @return returns the initial page number, or null if no initial page number has been specified
	 */
	public Integer getInitialPageNumber() {
		return initialPagenum;
	}
	
	/**
	 * Gets the minimum number of rows that the specified block requires to begin 
	 * rendering on a page.
	 * 
	 * @param block the block to get the 
	 * @param refs
	 * @return the minimum number of rows
	 */
	public int getKeepHeight(Block block, BlockContext bc) {
		return getKeepHeight(this.indexOf(block), bc);
	}
	private int getKeepHeight(int gi, BlockContext bc) {
		//FIXME: this assumes that row spacing is equal to 1
		//FIXME: what about borders?
		int keepHeight = getBlock(gi).getRowDataProperties().getOuterSpaceBefore()+getBlock(gi).getRowDataProperties().getInnerSpaceBefore()+getBlock(gi).getBlockContentManager(bc).getRowCount();
		if (getBlock(gi).getKeepWithNext()>0 && gi+1<getBlockCount()) {
			keepHeight += getBlock(gi).getRowDataProperties().getOuterSpaceAfter()+getBlock(gi).getRowDataProperties().getInnerSpaceAfter()
						+getBlock(gi+1).getRowDataProperties().getOuterSpaceBefore()+getBlock(gi+1).getRowDataProperties().getInnerSpaceBefore()+getBlock(gi).getKeepWithNext();
			switch (getBlock(gi+1).getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(gi+1, bc);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}
	
	@Override
	public void startTable(TableProperties props) {
		if (table!=null) {
			throw new IllegalStateException("A table is already open.");
		}
		if (!propsContext.empty()) {
			throw new IllegalStateException("Tables are not allowed inside blocks.");
		}
		//FIXME: row data properties
		String lb = "";
		String rb = "";
		TextBorderStyle borderStyle = null;
		if (props.getBorder()!=null) {
			Border b = props.getBorder();
			TextBorderFactoryMakerService tbf = fc.getTextBorderFactoryMakerService();
			Map<String, Object> features = new HashMap<String, Object>();
			features.put(TextBorderFactory.FEATURE_MODE, fc.getTranslatorMode());
			features.put("border", b);
			try {
				borderStyle = tbf.newTextBorderStyle(features);
			} catch (TextBorderConfigurationException e) {
				logger.log(Level.WARNING, "Failed to add border: " + b, e);
			}
		}
		if (borderStyle!=null) {
			lb = borderStyle.getLeftBorder();
			rb = borderStyle.getRightBorder();
		}
		Margin leftMargin = new Margin(Type.LEFT);
		Margin rightMargin = new Margin(Type.RIGHT);
		leftMargin.add(new MarginComponent(lb, props.getMargin().getLeftSpacing(), props.getPadding().getLeftSpacing()));
		rightMargin.add(new MarginComponent(rb, props.getMargin().getRightSpacing(), props.getPadding().getRightSpacing()));
		RowDataProperties.Builder rdp = new RowDataProperties.Builder()
				.leftMargin((Margin)leftMargin.clone())
				.rightMargin((Margin)rightMargin.clone())
				.outerSpaceBefore(props.getMargin().getTopSpacing())
				.outerSpaceAfter(props.getMargin().getBottomSpacing())
				.innerSpaceBefore(props.getPadding().getTopSpacing())
				.innerSpaceAfter(props.getPadding().getBottomSpacing());
		if (borderStyle!=null) {
			if (borderStyle.getTopLeftCorner().length()+borderStyle.getTopBorder().length()+borderStyle.getTopRightCorner().length()>0) {
				rdp.leadingDecoration(new SingleLineDecoration(borderStyle.getTopLeftCorner(), borderStyle.getTopBorder(), borderStyle.getTopRightCorner()));
			}
			if (borderStyle.getBottomLeftCorner().length()+ borderStyle.getBottomBorder().length()+ borderStyle.getBottomRightCorner().length()>0) {
				rdp.trailingDecoration(new SingleLineDecoration(borderStyle.getBottomLeftCorner(), borderStyle.getBottomBorder(), borderStyle.getBottomRightCorner()));
			}
		}
		table = new Table(props, rdp.build(), fc.getTextBorderFactoryMakerService(), fc.getTranslatorMode(), scenario);
		add(table);
	}

	@Override
	public void beginsTableHeader() {
		//no action, header is assumed in the implementation
	}

	@Override
	public void beginsTableBody() {
		table.beginsTableBody();
	}

	@Override
	public void beginsTableRow() {
		table.beginsTableRow();
	}

	@Override
	public FormatterCore beginsTableCell(TableCellProperties props) {
		return table.beginsTableCell(props);
	}

	@Override
	public void endTable() {
		table = null;
	}

}
