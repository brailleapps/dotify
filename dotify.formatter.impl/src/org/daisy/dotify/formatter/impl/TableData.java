package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;

class TableData implements Iterable<TableRow> {
	private final Stack<TableRow> rows;
	private Map<GridPoint, TableCell> grid;
	private int rh, gy;

	TableData() {
		rows = new Stack<>();
		grid = new HashMap<>();
		rh = 0;
		gy = 0;
	}
	
	void beginsTableRow() {
		if (!rows.empty()) {
			rows.peek().endsTableCell();
		}
		gy += rh;
		rh = 1;
		TableRow ret = new TableRow();
		rows.add(ret);
		
	}

	FormatterCore beginsTableCell(TableCellProperties props) {
		rh = Math.min(rh, props.getRowSpan());
		int r = gy;
		int c = rows.peek().cellCount();
		while (grid.get(new GridPoint(r, c))!=null) {
			c++;
		}
		TableCell ret = rows.peek().beginsTableCell(props, new GridPoint(r, c));
		for (int i=0; i<props.getRowSpan(); i++) {
			for (int j=0; j<props.getColSpan(); j++) {
				GridPoint p = new GridPoint(r+i, c+j);
				if (grid.containsKey(p)) {
					//TODO: throw checked exception? OR auto fix table
					throw new RuntimeException("Conflicting col-span/row-span.");
				}
				grid.put(p, ret);
			}
		}
		return ret;
	}
	
	TableCell getCurrentCell() {
		return rows.peek().getCurrentCell();
	}
	
	TableCell cellForGrid(int r, int c) {
		return grid.get(new GridPoint(r, c));
	}
	
	TableCell cellForIndex(int r, int c) {
		return getRow(r).getCell(c);
	}
	
	/*
	int getGridWidth() {
		return 0;
	}
	
	int getGridHeight() {
		return 0;
	}*/
	 
	int getRowCount() {
		return rows.size();
	}
	
	TableRow getRow(int i) {
		return rows.get(i);
	}

	@Override
	public Iterator<TableRow> iterator() {
		return rows.iterator();
	}


}
