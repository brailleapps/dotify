package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;

class Table {
	private int headerRows;
	private final Stack<TableRow> rows;

	Table() {
		headerRows = 0;
		rows = new Stack<>();
	}

	public void beginsTableBody() {
		headerRows = rows.size();
	}

	public void beginsTableRow() {
		if (!rows.empty()) {
			rows.peek().endsTableCell();
		}
		rows.add(new TableRow());
	}

	public FormatterCore beginsTableCell(TableCellProperties props) {
		return rows.peek().beginsTableCell(props);
	}

}
