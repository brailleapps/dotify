package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.TableCellProperties;
import org.daisy.dotify.api.translator.Border;

class TableCell extends FormatterCoreImpl {
	/**
	 * 
	 */
	private static final long serialVersionUID = -673589204065659433L;
	private final int rowSpan, colSpan;
	private final Border border;

	TableCell(TableCellProperties props) {
		this(props, false);
	}
	
	TableCell(TableCellProperties props, boolean discardIdentifiers) {
		super(discardIdentifiers);
		this.rowSpan = props.getRowSpan();
		this.colSpan = props.getColSpan();
		this.border = props.getBorder();
	}

	int getRowSpan() {
		return rowSpan;
	}

	int getColSpan() {
		return colSpan;
	}

	Border getBorder() {
		return border;
	}
}
