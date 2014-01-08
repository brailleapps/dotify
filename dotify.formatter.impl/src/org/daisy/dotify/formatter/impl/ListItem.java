package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.FormattingTypes;

class ListItem {
	private String label;
	private FormattingTypes.ListStyle type;
	
	public ListItem(String label, FormattingTypes.ListStyle type) {
		this.label = label;
		this.type = type;
	}
	
	public String getLabel() {
		return label;
	}
	
	public FormattingTypes.ListStyle getType() {
		return type;
	}
}