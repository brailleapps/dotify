package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.FormattingTypes.Alignment;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.translator.BrailleTranslator;

class RowDataProperties {
	private final int blockIndent, blockIndentParent;
	private final FormatterContext context;
	private final LayoutMaster master;
	private final MarginProperties leftMargin;
	private final MarginProperties rightMargin;
	private final MarginProperties leftMarginParent;
	private final MarginProperties rightMarginParent;
	private final ListItem listProps;
	private final int textIndent;
	private final int firstLineIndent;
	private final Alignment align;
	private final Float rowSpacing;
	
	static class Builder {
		private final FormatterContext context;
		private final LayoutMaster master;
		private int blockIndent = 0;
		private int blockIndentParent = 0;
		private int textIndent = 0;
		private int firstLineIndent = 0;
		private Alignment align = Alignment.LEFT;
		private Float rowSpacing = null;
		private MarginProperties leftMargin = new MarginProperties();
		private MarginProperties rightMargin = new MarginProperties();
		private MarginProperties leftMarginParent = new MarginProperties();
		private MarginProperties rightMarginParent = new MarginProperties();
		private ListItem listProps = null;
		
		public Builder(FormatterContext context, LayoutMaster master) {
			this.context = context;
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
		
		public Builder textIndent(int textIndent) {
			this.textIndent = textIndent;
			return this;
		}
		
		public Builder firstLineIndent(int firstLineIndent) {
			this.firstLineIndent = firstLineIndent;
			return this;
		}
		
		public Builder align(FormattingTypes.Alignment align) {
			this.align = align;
			return this;
		}
		
		public Builder rowSpacing(Float value) {
			this.rowSpacing = value;
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
		this.context = builder.context;
		this.master = builder.master;
		this.leftMargin = builder.leftMargin;
		this.rightMargin = builder.rightMargin;
		this.leftMarginParent = builder.leftMarginParent;
		this.rightMarginParent = builder.rightMarginParent;
		this.listProps = builder.listProps;
		this.textIndent = builder.textIndent;
		this.firstLineIndent = builder.firstLineIndent;
		this.align = builder.align;
		this.rowSpacing = builder.rowSpacing;
	}

	public int getBlockIndent() {
		return blockIndent;
	}

	public int getBlockIndentParent() {
		return blockIndentParent;
	}
	
	public int getTextIndent() {
		return textIndent;
	}
	
	public int getFirstLineIndent() {
		return firstLineIndent;
	}
	
	public FormattingTypes.Alignment getAlignment() {
		return align;
	}
	
	public Float getRowSpacing() {
		return rowSpacing;
	}

	/*
	public StringFilter getFilter() {
		return filter;
	}*/
	
	public BrailleTranslator getTranslator() {
		return context.getTranslator();
	}

	public LayoutMaster getMaster() {
		return master;
	}
	
	public char getSpaceCharacter() {
		return context.getSpaceCharacter();
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
