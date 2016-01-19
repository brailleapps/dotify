package org.daisy.dotify.formatter.impl;

import java.util.Iterator;
import java.util.List;

public class TableBlockContentManager extends AbstractBlockContentManager {
	private final List<RowImpl> rows;

	public TableBlockContentManager(int flowWidth, List<RowImpl> rows, RowDataProperties rdp, FormatterContext fcontext) {
		super(flowWidth, rdp, fcontext);
		this.rows = rows;
	}
	
	@Override
	public Iterator<RowImpl> iterator() {
		return rows.iterator();
	}

	@Override
	public int getRowCount() {
		return rows.size();
	}

}
