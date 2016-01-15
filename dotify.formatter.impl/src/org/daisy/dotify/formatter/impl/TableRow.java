package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.formatter.TextBlockProperties;

class TableRow {
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
				.rowSpacing(tbp.getRowSpacing())
				.build());
		cells.push(fc);
		return fc;
	}
	
	void endsTableCell() {
		if (!cells.empty()) {
			cells.peek().endBlock();
		}
	}

}
