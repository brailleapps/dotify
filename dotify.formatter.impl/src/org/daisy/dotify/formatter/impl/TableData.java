package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;

class TableData implements Iterable<TableRow> {
	private final Stack<TableRow> rows;
	private Map<Point, TableCell> grid;
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
		while (grid.get(new Point(r, c))!=null) {
			c++;
		}
		TableCell ret = rows.peek().beginsTableCell(props);
		for (int i=0; i<props.getRowSpan(); i++) {
			for (int j=0; j<props.getColSpan(); j++) {
				grid.put(new Point(r+i, c+j), ret);
			}
		}
		return ret;
	}
	
	TableCell getCurrentCell() {
		return rows.peek().getCurrentCell();
	}
	
	TableCell cellForGrid(int r, int c) {
		return grid.get(new Point(r, c));
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

	private static class Point {
		private final int row,  col;
		
		Point(int r, int c) {
			this.row = r;
			this.col = c;
		}

		@Override
		public String toString() {
			return "Coord [row=" + row + ", col=" + col + "]";
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + col;
			result = prime * result + row;
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj) {
				return true;
			}
			if (obj == null) {
				return false;
			}
			if (getClass() != obj.getClass()) {
				return false;
			}
			Point other = (Point) obj;
			if (col != other.col) {
				return false;
			}
			if (row != other.row) {
				return false;
			}
			return true;
		}
		
	}
}
