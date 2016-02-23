package org.daisy.dotify.formatter.impl;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TextBlockProperties;

class TableRow implements Iterable<TableCell> {
	private final Stack<TableCell> cells;
	private final FormatterCoreContext context;

	TableRow(FormatterCoreContext fc) {
		this.context = fc;
		cells = new Stack<>();
	}
	
	TableCell beginsTableCell(TableCellProperties props, GridPoint p) {
		TableCell fc = new TableCell(context, props, p);
		TextBlockProperties tbp = props.getTextBlockProperties();
		fc.startBlock(new BlockProperties.Builder()
				.bottomPadding(props.getPadding().getBottomSpacing())
				.topPadding(props.getPadding().getTopSpacing())
				.leftPadding(props.getPadding().getLeftSpacing())
				.rightPadding(props.getPadding().getRightSpacing())
				.align(tbp.getAlignment())
				.firstLineIndent(tbp.getFirstLineIndent())
				.textIndent(tbp.getTextIndent())
				.identifier(tbp.getIdentifier())
				.build());
		cells.push(fc);
		return fc;
	}
	
	/**
	 * 
	 * @return
	 * @throws IllegalStateException if the row is empty
	 */
	TableCell getCurrentCell() {
		try {
			return cells.peek();
		} catch (EmptyStackException e) {
			throw new IllegalStateException(e);
		}
	}
	
	void endsTableCell() {
		if (!cells.empty()) {
			cells.peek().endBlock();
		}
	}
	
	int cellCount() {
		return cells.size();
	}
	
	TableCell getCell(int index) {
		return cells.get(0);
	}

	@Override
	public Iterator<TableCell> iterator() {
		return cells.iterator();
	}

}
