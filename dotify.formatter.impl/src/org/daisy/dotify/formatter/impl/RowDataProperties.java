package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.translator.BrailleTranslator;

class RowDataProperties {
	private final int blockIndent, blockIndentParent;
	private final BrailleTranslator translator;
	private final LayoutMaster master;
	private final int leftMargin, rightMargin;
	private ListItem listProps;
	
	static class Builder {
		private final BrailleTranslator translator;
		private final LayoutMaster master;
		private int blockIndent = 0;
		private int blockIndentParent = 0;
		private int leftMargin = 0;
		private int rightMargin = 0;
		
		public Builder(BrailleTranslator translator, LayoutMaster master) {
			this.translator = translator;
			this.master = master;
		}
		
		public Builder blockIndent(int value) {
			blockIndent = value;
			return this;
		}
		
		public Builder blockIndentParent(int value) {
			blockIndentParent = value;
			return this;
		}
		public Builder leftMargin(int value) {
			leftMargin = value;
			return this;
		}
		
		public Builder rightMargin(int value) {
			rightMargin = value;
			return this;
		}
		public RowDataProperties build() {
			return new RowDataProperties(this);
		}
	}
	
	private RowDataProperties(Builder builder) {
		this.blockIndent = builder.blockIndent;
		this.blockIndentParent = builder.blockIndentParent;
		this.translator = builder.translator;
		this.master = builder.master;
		this.leftMargin = builder.leftMargin;
		this.rightMargin = builder.rightMargin;
		this.listProps = null;
	}

	public int getBlockIndent() {
		return blockIndent;
	}

	public int getBlockIndentParent() {
		return blockIndentParent;
	}

	/*
	public StringFilter getFilter() {
		return filter;
	}*/
	
	public BrailleTranslator getTranslator() {
		return translator;
	}

	public LayoutMaster getMaster() {
		return master;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public int getRightMargin() {
		return rightMargin;
	}
	
	public void setListItem(String label, FormattingTypes.ListStyle type) {
		listProps = new ListItem(label, type);
	}
	
	public boolean isList() {
		return listProps!=null;
	}
	
	ListItem getListItem() {
		return listProps;
	}

}
