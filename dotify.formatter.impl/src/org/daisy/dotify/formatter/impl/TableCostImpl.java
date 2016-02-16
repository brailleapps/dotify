package org.daisy.dotify.formatter.impl;

import java.util.List;

public class TableCostImpl implements TableCost {
	private final int spacePreferred;
	private double cost;

	public TableCostImpl(int spacePreferred) {
		this.spacePreferred = spacePreferred;
		this.cost = 0;
	}

	@Override
	public double getCost() {
		return cost;
	}

	@Override
	public void addCell(List<RowImpl> rows, int cellWidth) {
		int len = 0;
		for (RowImpl r : rows) {
			len = Math.max(r.getWidth(), len);
		}
		cost += rows.isEmpty()?0:preferredSpaceCost(len, cellWidth);
	}

	private int preferredSpaceCost(int r, int cellWidth) { 
		return Math.abs((cellWidth-r)-spacePreferred);
	}

	@Override
	public void completeTable(List<RowImpl> rows, int columnCount) {
		cost += columnCount*rows.size();
	}

}
