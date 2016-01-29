package org.daisy.dotify.formatter.impl;

import java.util.List;

class CellData {
	private final List<RowImpl> rows;
	private final int colSpan;
	private final int cellWidth;
	private final int startIndex;

	CellData(List<RowImpl> rows, int colSpan, int cellWidth, int startIndex) {
		this.rows = rows;
		this.colSpan = colSpan;
		this.cellWidth = cellWidth;
		this.startIndex = startIndex;
	}

	public List<RowImpl> getRows() {
		return rows;
	}

	public int getColSpan() {
		return colSpan;
	}

	public int getCellWidth() {
		return cellWidth;
	}

	public int getStartIndex() {
		return startIndex;
	}
	
	
}