package org.daisy.dotify.formatter.impl;

import java.util.EmptyStackException;
import java.util.Iterator;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TextBlockProperties;

class TableRow implements Iterable<TableCell> {
	private final Stack<TableCell> cells;

	TableRow() {
		cells = new Stack<>();
	}
	
	FormatterCore beginsTableCell(TableCellProperties props) {
		TableCell fc = new TableCell(props);
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

	@Override
	public Iterator<TableCell> iterator() {
		return cells.iterator();
	}

}
