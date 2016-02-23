package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.TableCellProperties;

class TableData implements Iterable<TableRow> {
	private final Stack<TableRow> rows;
	private final Map<GridPoint, TableCell> grid;
	private int rh, gy, rMax, cMax;
	private final FormatterCoreContext fc;

	TableData(FormatterCoreContext fc) {
		this.fc = fc;
		rows = new Stack<>();
		grid = new HashMap<>();
		rh = 0;
		rMax = 0;
		cMax = 0;
		gy = 0;
	}
	
	void beginsTableRow() {
		if (!rows.empty()) {
			rows.peek().endsTableCell();
		}
		gy += rh;
		rh = 1;
		rMax = 1;
		TableRow ret = new TableRow(fc);
		rows.add(ret);
		
	}

	FormatterCore beginsTableCell(TableCellProperties props) {
		rh = Math.min(rh, props.getRowSpan());
		rMax = Math.max(rMax, props.getRowSpan());
		int r = gy;
		int c = rows.peek().cellCount(); // this is just a starting point, we know for sure that c cannot be less than this
		while (grid.get(new GridPoint(r, c))!=null) {
			c++;
		}
		cMax = Math.max(cMax, c+props.getColSpan());
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

	public int getGridHeight() {
		return gy+rMax;
	}
	
	public int getGridWidth() {
		return cMax;
	}

}
