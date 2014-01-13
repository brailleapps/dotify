package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.translator.BrailleTranslator;

class RowDataProperties {
	private final int blockIndent, blockIndentParent;
	private final BrailleTranslator translator;
	private final LayoutMaster master;
	private final MarginProperties leftMargin;
	private final MarginProperties rightMargin;
	private final MarginProperties leftMarginParent;
	private final MarginProperties rightMarginParent;
	private final ListItem listProps;
	
	static class Builder {
		private final BrailleTranslator translator;
		private final LayoutMaster master;
		private int blockIndent = 0;
		private int blockIndentParent = 0;
		private MarginProperties leftMargin = new MarginProperties();
		private MarginProperties rightMargin = new MarginProperties();
		private MarginProperties leftMarginParent = new MarginProperties();
		private MarginProperties rightMarginParent = new MarginProperties();
		private ListItem listProps = null;
		
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
		public Builder leftMargin(MarginProperties value) {
			leftMargin = value;
			return this;
		}
		
		public Builder rightMargin(MarginProperties value) {
			rightMargin = value;
			return this;
		}
		
		public Builder leftMarginParent(MarginProperties value) {
			leftMarginParent = value;
			return this;
		}
		public Builder rightMarginParent(MarginProperties value) {
			rightMarginParent = value;
			return this;
		}
		public Builder listProperties(ListItem value) {
			listProps = value;
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
		this.leftMarginParent = builder.leftMarginParent;
		this.rightMarginParent = builder.rightMarginParent;
		this.listProps = builder.listProps;
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

	public MarginProperties getLeftMargin() {
		return leftMargin;
	}

	public MarginProperties getRightMargin() {
		return rightMargin;
	}
	
	public MarginProperties getLeftMarginParent() {
		return leftMarginParent;
	}
	public MarginProperties getRightMarginParent() {
		return rightMarginParent;
	}
	public boolean isList() {
		return listProps!=null;
	}
	
	ListItem getListItem() {
		return listProps;
	}

}
