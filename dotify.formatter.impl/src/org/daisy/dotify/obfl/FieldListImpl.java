package org.daisy.dotify.obfl;

import java.util.List;

import org.daisy.dotify.api.formatter.Field;
import org.daisy.dotify.api.formatter.FieldList;

class FieldListImpl implements FieldList {
	private final List<Field> contents;
	private Float rowSpacing;
	
	public FieldListImpl(List<Field> contents) {
		this.contents = contents;
		this.rowSpacing = null;
	}

	public Float getRowSpacing() {
		return rowSpacing;
	}

	public void setRowSpacing(Float rowSpacing) {
		this.rowSpacing = rowSpacing;
	}

	public List<Field> getFields() {
		return contents;
	}

}
