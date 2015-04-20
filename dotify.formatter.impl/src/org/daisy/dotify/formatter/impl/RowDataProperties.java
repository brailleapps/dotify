package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.FormattingTypes.Alignment;
import org.daisy.dotify.formatter.impl.Margin.Type;

class RowDataProperties {
	private final int blockIndent, blockIndentParent;
	private final Margin leftMargin;
	private final Margin rightMargin;
	private final ListItem listProps;
	private final int textIndent;
	private final int firstLineIndent;
	private final Alignment align;
	private final Float rowSpacing;
	private final int spaceBefore;
	private final int spaceAfter;
	private final SingleLineDecoration leadingDecoration;
	private final SingleLineDecoration trailingDecoration;
	
	static class Builder {
		private int blockIndent = 0;
		private int blockIndentParent = 0;
		private int textIndent = 0;
		private int firstLineIndent = 0;
		private int spaceBefore = 0;
		private int spaceAfter = 0;
		private Alignment align = Alignment.LEFT;
		private Float rowSpacing = null;
		private Margin leftMargin = new Margin(Type.LEFT);
		private Margin rightMargin = new Margin(Type.RIGHT);
		private SingleLineDecoration leadingDecoration = null;
		private SingleLineDecoration trailingDecoration = null;

		private ListItem listProps = null;
		
		public Builder() {
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
		
		public Builder leftMargin(Margin value) {
			leftMargin = value;
			return this;
		}
		
		public Builder rightMargin(Margin value) {
			rightMargin = value;
			return this;
		}

		public Builder listProperties(ListItem value) {
			listProps = value;
			return this;
		}
		
		public int getSpaceBefore() {
			return spaceBefore;
		}
		
		public int getSpaceAfter() {
			return spaceAfter;
		}
		
		public void addSpaceBefore(int spaceBefore) {
			this.spaceBefore += spaceBefore;
		}
		
		public void addSpaceAfter(int spaceAfter) {
			this.spaceAfter += spaceAfter;
		}
		
		public SingleLineDecoration getLeadingDecoration() {
			return leadingDecoration;
		}
		
		public void setLeadingDecoration(SingleLineDecoration value) {
			this.leadingDecoration = value;
		}
		
		public SingleLineDecoration getTrailingDecoration() {
			return trailingDecoration;
		}

		public void setTrailingDecoration(SingleLineDecoration value) {
			this.trailingDecoration = value;
		}

		public RowDataProperties build() {
			return new RowDataProperties(this);
		}
	}
	
	private RowDataProperties(Builder builder) {
		this.blockIndent = builder.blockIndent;
		this.blockIndentParent = builder.blockIndentParent;
		this.leftMargin = builder.leftMargin;
		this.rightMargin = builder.rightMargin;

		this.listProps = builder.listProps;
		this.textIndent = builder.textIndent;
		this.firstLineIndent = builder.firstLineIndent;
		this.align = builder.align;
		this.rowSpacing = builder.rowSpacing;
		this.spaceBefore = builder.spaceBefore;
		this.spaceAfter = builder.spaceAfter;
		this.leadingDecoration = builder.leadingDecoration;
		this.trailingDecoration = builder.trailingDecoration;
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

	public Margin getLeftMargin() {
		return leftMargin;
	}

	public Margin getRightMargin() {
		return rightMargin;
	}
	public boolean isList() {
		return listProps!=null;
	}
	
	ListItem getListItem() {
		return listProps;
	}

	public int getSpaceBefore() {
		return spaceBefore;
	}

	public int getSpaceAfter() {
		return spaceAfter;
	}

	public SingleLineDecoration getLeadingDecoration() {
		return leadingDecoration;
	}

	public SingleLineDecoration getTrailingDecoration() {
		return trailingDecoration;
	}

}
