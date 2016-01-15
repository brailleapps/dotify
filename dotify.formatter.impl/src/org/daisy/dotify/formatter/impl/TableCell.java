package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.TableCellProperties;

class TableCell extends FormatterCoreImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -673589204065659433L;
	private final int rowSpan, colSpan;

	TableCell(TableCellProperties props) {
		this(props, false);
	}
	
	TableCell(TableCellProperties props, boolean discardIdentifiers) {
		super(discardIdentifiers);
		this.rowSpan = props.getRowSpan();
		this.colSpan = props.getColSpan();
	}

	int getRowSpan() {
		return rowSpan;
	}

	int getColSpan() {
		return colSpan;
	}


}
