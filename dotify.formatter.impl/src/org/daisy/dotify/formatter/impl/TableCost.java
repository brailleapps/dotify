package org.daisy.dotify.formatter.impl;

import java.util.List;

public interface TableCost {

	public int getCost();
	
	public void addCell(List<RowImpl> rows, int cellWidth);
	
	public void completeTable(List<RowImpl> rows);
}
