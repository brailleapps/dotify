package org.daisy.dotify.formatter.impl;

import java.util.List;

public class TableCostImpl implements TableCost {
	private final int spacePreferred;
	private int cost;

	public TableCostImpl(int spacePreferred) {
		this.spacePreferred = spacePreferred;
		this.cost = 0;
	}

	@Override
	public int getCost() {
		return cost;
	}

	@Override
	public void addCell(List<RowImpl> rows, int cellWidth) {
		int len = 0;
		for (RowImpl r : rows) {
			len = Math.max(r.getChars().length()+r.getLeftMargin().getContent().length()+r.getRightMargin().getContent().length(), len);
		}
		cost += rows.isEmpty()?0:preferredSpaceCost(len, cellWidth);
	}

	private int preferredSpaceCost(int r, int cellWidth) { 
		return Math.abs((cellWidth-r)-spacePreferred);
	}

	@Override
	public void completeTable(List<RowImpl> rows) {
		cost += 10*rows.size();
	}

}
