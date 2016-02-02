package org.daisy.dotify.formatter.impl;

import java.util.Iterator;
import java.util.List;

class CellData {
	private final List<RowImpl> rows;
	private final TableCellInfo info;
	private final int cellWidth;
	private Iterator<RowImpl> rowsIterator;

	CellData(List<RowImpl> rows, int cellWidth, TableCellInfo info) {
		this.rows = rows;
		this.info = info;
		this.cellWidth = cellWidth;
		this.rowsIterator = rows.iterator();
	}
	
	List<RowImpl> getRows() {
		return rows;
	}

	/**
	 * Gets a consumable row iterator
	 * @return
	 */
	Iterator<RowImpl> getRowIterator() {
		return rowsIterator;
	}
	
	void restartIterator() {
		rowsIterator = rows.iterator();
	}

	int getCellWidth() {
		return cellWidth;
	}
	
	TableCellInfo getInfo() {
		return info;
	}
	
}