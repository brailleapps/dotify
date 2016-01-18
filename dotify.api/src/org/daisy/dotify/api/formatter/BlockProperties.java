package org.daisy.dotify.api.formatter;

import org.daisy.dotify.api.formatter.BlockPosition.VerticalAlignment;
import org.daisy.dotify.api.formatter.FormattingTypes.BreakBefore;
import org.daisy.dotify.api.formatter.FormattingTypes.Keep;
import org.daisy.dotify.api.formatter.FormattingTypes.ListStyle;
import org.daisy.dotify.api.translator.TextBorderStyle;


/**
 * BlockProperties defines properties specific for a block of text
 * @author Joel Håkansson
 */
public class BlockProperties implements Cloneable {
	private final BlockSpacing margin;
	private final BlockSpacing padding;
	private final ListStyle listType;
	private int listIterator;
	private final BreakBefore breakBefore;
	private final Keep keep;
	private final int orphans;
	private final int widows;
	private final int keepWithNext;
	private final int keepWithPreviousSheets;
	private final int keepWithNextSheets;
	private final int blockIndent;
	private final BlockPosition verticalPosition;
	private final TextBorderStyle textBorderStyle;
	private final TextBlockProperties textBlockProps;

	/**
	 * The Builder is used when creating a BlockProperties instance.
	 * @author Joel Håkansson
	 */
	public static class Builder {
		// Optional parameters
		private int leftMargin = 0;
		private int rightMargin = 0;
		private int topMargin = 0;
		private int bottomMargin = 0;
		private int leftPadding = 0;
		private int rightPadding = 0;
		private int topPadding = 0;
		private int bottomPadding = 0;
		private ListStyle listType = ListStyle.NONE;
		private BreakBefore breakBefore = BreakBefore.AUTO;
		private Keep keep = Keep.AUTO;
		private int orphans = 1;
		private int widows = 1;
		private int keepWithNext = 0;
		private int keepWithPreviousSheets = 0;
		private int keepWithNextSheets = 0;
		private int blockIndent = 0;
		private Position verticalPosition = null;
		private VerticalAlignment verticalAlignment = VerticalAlignment.AFTER;
		private TextBorderStyle textBorderStyle = null;
		private TextBlockProperties.Builder textBlockPropsBuilder = new TextBlockProperties.Builder();
		
		/**
		 * Create a new Builder
		 */
		public Builder() { }

		/**
		 * Set the left margin for the block, in characters.
		 * @param leftMargin left margin, in characters
		 * @return returns "this" object
		 */
		public Builder leftMargin(int leftMargin) {
			this.leftMargin = leftMargin;
			return this;
		}
		
		/**
		 * Set the right margin for the block, in characters.
		 * @param rightMargin right margin, in characters
		 * @return returns "this" object
		 */
		public Builder rightMargin(int rightMargin) {
			this.rightMargin = rightMargin;
			return this;
		}
		
		/**
		 * Set the top margin for the block, in characters.
		 * @param topMargin top margin, in characters
		 * @return returns "this" object
		 */
		public Builder topMargin(int topMargin) {
			this.topMargin = topMargin;
			return this;
		}
		
		/**
		 * Set the bottom margin for the block, in characters.
		 * @param bottomMargin bottom margin, in characters
		 * @return returns "this" object
		 */
		public Builder bottomMargin(int bottomMargin) {
			this.bottomMargin = bottomMargin;
			return this;
		}
		
		/**
		 * Set the left padding for the block, in characters.
		 * @param leftPadding left padding, in characters
		 * @return returns "this" object
		 */
		public Builder leftPadding(int leftPadding) {
			this.leftPadding = leftPadding;
			return this;
		}
		
		/**
		 * Set the right padding for the block, in characters.
		 * @param rightPadding right padding, in characters
		 * @return returns "this" object
		 */
		public Builder rightPadding(int rightPadding) {
			this.rightPadding = rightPadding;
			return this;
		}
		
		/**
		 * Set the top padding for the block, in characters.
		 * @param topPadding top padding, in characters
		 * @return returns "this" object
		 */
		public Builder topPadding(int topPadding) {
			this.topPadding = topPadding;
			return this;
		}
		
		/**
		 * Set the bottom padding for the block, in characters.
		 * @param bottomPadding bottom padding, in characters
		 * @return returns "this" object
		 */
		public Builder bottomPadding(int bottomPadding) {
			this.bottomPadding = bottomPadding;
			return this;
		}
		
		/**
		 * Set the text indent for the block, in characters.
		 * The text indent controls the indent of all text rows except 
		 * the first one, see {@link #firstLineIndent(int)}.
		 * The text indent is applied to text directly within 
		 * the block, but is not inherited to block children.
		 * @param textIndent the indent, in characters
		 * @return returns "this" object
		 */
		public Builder textIndent(int textIndent) {
			this.textBlockPropsBuilder.textIndent(textIndent);
			return this;
		}
		
		/**
		 * Set the first line indent for the block, in characters.
		 * The first line indent controls the indent of the first text 
		 * row in a block.
		 * The first line indent is applied to text directly within
		 * the block, but is not inherited to block children.
		 * @param firstLineIndent the indent, in characters.
		 * @return returns "this" object
		 */
		public Builder firstLineIndent(int firstLineIndent) {
			this.textBlockPropsBuilder.firstLineIndent(firstLineIndent);
			return this;
		}
		
		/**
		 * Set the list type for the block. The list type is
		 * applied to block's children.
		 * @param listType the type of list
		 * @return returns "this" object
		 */
		public Builder listType(FormattingTypes.ListStyle listType) {
			this.listType = listType;
			return this;
		}
		
		/**
		 * Set the break before property for the block.
		 * @param breakBefore the break before type
		 * @return returns "this" object
		 */
		public Builder breakBefore(FormattingTypes.BreakBefore breakBefore) {
			this.breakBefore = breakBefore;
			return this;
		}
		
		/**
		 * Set the keep property for the block.
		 * @param keep the keep type
		 * @return returns "this" object
		 */
		public Builder keep(FormattingTypes.Keep keep) {
			this.keep = keep;
			return this;
		}
		
		/**
		 * Sets the alignment property for the block.
		 * @param align the alignment
		 * @return returns "this" object
		 */
		public Builder align(FormattingTypes.Alignment align) {
			this.textBlockPropsBuilder.align(align);
			return this;
		}
		
		/**
		 * Set the keep with next property for the block.
		 * @param keepWithNext the number of rows in the next 
		 * block to keep together with this block
		 * @return returns "this" object
		 */
		public Builder keepWithNext(int keepWithNext) {
			this.keepWithNext = keepWithNext;
			return this;
		}
		
		public Builder keepWithPreviousSheets(int keepWithPreviousSheets) {
			this.keepWithPreviousSheets = keepWithPreviousSheets;
			return this;
		}
		
		public Builder keepWithNextSheets(int keepWithNextSheets) {
			this.keepWithNextSheets = keepWithNextSheets;
			return this;
		}
		
		public Builder verticalPosition(Position position) {
			verticalPosition = position;
			return this;
		}

		public Builder verticalAlignment(BlockPosition.VerticalAlignment alignment) {
			verticalAlignment = alignment;
			return this;
		}
		
		/**
		 * 
		 * @param value
		 * @return returns the row spacing
		 */
		public Builder rowSpacing(float value) {
			this.textBlockPropsBuilder.rowSpacing(value);
			return this;
		}

		/**
		 * Set the block indent for the block, in characters.
		 * The block indent controls the indent of child blocks
		 * in a block. This is useful when building lists.
		 * @param blockIndent the indent, in characters
		 * @return returns "this" object
		 */
		public Builder blockIndent(int blockIndent) {
			this.blockIndent = blockIndent;
			return this;
		}
		
		/**
		 * 
		 * @param identifier
		 * @return
		 */
		public Builder identifier(String identifier) {
			this.textBlockPropsBuilder.identifier(identifier);
			return this;
		}

		public Builder textBorderStyle(TextBorderStyle value) {
			this.textBorderStyle = value;
			return this;
		}
		
		/**
		 * Sets the orphans property
		 * @param value the value
		 * @return returns this object
		 */
		public Builder orphans(int value) {
			this.orphans = value;
			return this;
		}
		
		/**
		 * Sets the widows property
		 * @param value the value
		 * @return returns this object
		 */
		public Builder widows(int value) {
			this.widows = value;
			return this;
		}
		
		/**
		 * Build BlockProperties using the current state of the Builder.
		 * @return returns a new BlockProperties instance
		 */
		public BlockProperties build() {
			return new BlockProperties(this);
		}
	}

	protected BlockProperties(Builder builder) {
		margin = new BlockSpacing.Builder().
				leftSpacing(builder.leftMargin).
				rightSpacing(builder.rightMargin).
				topSpacing(builder.topMargin).
				bottomSpacing(builder.bottomMargin).build();
		padding = new BlockSpacing.Builder().
				leftSpacing(builder.leftPadding).
				rightSpacing(builder.rightPadding).
				topSpacing(builder.topPadding).
				bottomSpacing(builder.bottomPadding).build();
		textBlockProps = builder.textBlockPropsBuilder.build();
		listType = builder.listType;
		listIterator = 0;
		breakBefore = builder.breakBefore;
		keep = builder.keep;
		orphans = builder.orphans;
		widows = builder.widows;
		keepWithNext = builder.keepWithNext;
		keepWithPreviousSheets = builder.keepWithPreviousSheets;
		keepWithNextSheets = builder.keepWithNextSheets;
		blockIndent = builder.blockIndent;
		if (builder.verticalPosition != null) {
			verticalPosition = new BlockPosition.Builder().
				position(builder.verticalPosition).
				align(builder.verticalAlignment).build();
		} else {
			verticalPosition = null;
		}
		textBorderStyle = builder.textBorderStyle;
	}

	/**
	 * Gets the margin
	 * @return returns the margin
	 */
	public BlockSpacing getMargin() {
		return margin;
	}
	
	/**
	 * Gets the padding
	 * @return returns the padding
	 */
	public BlockSpacing getPadding() {
		return padding;
	}
	
	/**
	 * Get text indent, in characters
	 * @return returns the text indent
	 * @deprecated use <code>getTextBlockProperties</code>
	 */
	@Deprecated
	public int getTextIndent() {
		return textBlockProps.getTextIndent();
	}
	
	/**
	 * Get first line indent, in characters
	 * @return returns the first line indent
	 * @deprecated use <code>getTextBlockProperties</code>
	 */
	@Deprecated
	public int getFirstLineIndent() {
		return textBlockProps.getFirstLineIndent();
	}
	
	/**
	 * Get block indent, in characters
	 * @return returns the block indent
	 */
	public int getBlockIndent() {
		return blockIndent;
	}
	
	/**
	 * Get list type
	 * @return returns the list type
	 */
	public FormattingTypes.ListStyle getListType() {
		return listType;
	}
	
	/**
	 * Increments the list iterator and returns the current list number
	 * @return returns the current list number
	 * @deprecated keep state outside of this object
	 */
	@Deprecated
	public int nextListNumber() {
		listIterator++;
		return listIterator;
	}
	
	/**
	 * Get current list number
	 * @return returns the current list number
	 * @deprecated keep state outside of this object
	 */
	@Deprecated
	public int getListNumber() {
		return listIterator;
	}

	/**
	 * Get break before type
	 * @return returns the break before type
	 */
	public FormattingTypes.BreakBefore getBreakBeforeType() {
		return breakBefore;
	}

	/**
	 * Get keep type
	 * @return returns the keep type
	 */
	public FormattingTypes.Keep getKeepType() {
		return keep;
	}
	
	/**
	 * Gets the alignment
	 * @return returns the alignment
	 * @deprecated use <code>getTextBlockProperties</code>
	 */
	@Deprecated
	public FormattingTypes.Alignment getAlignment() {
		return textBlockProps.getAlignment();
	}

	/**
	 * Get the number of rows containing text in the next block that must be on the same page as this block
	 * @return returns the number of rows in the next block to keep with this block 
	 */
	public int getKeepWithNext() {
		return keepWithNext;
	}
	
	public int getKeepWithPreviousSheets() {
		return keepWithPreviousSheets;
	}
	
	public int getKeepWithNextSheets() {
		return keepWithNextSheets;
	}
	
	/**
	 * 
	 * @return the identifier
	 * @deprecated use <code>getTextBlockProperties</code>
	 */
	@Deprecated
	public String getIdentifier() {
		return textBlockProps.getIdentifier();
	}

	public BlockPosition getVerticalPosition() {
		return verticalPosition;
	}
	
	/**
	 * 
	 * @return returns the row spacing
	 * @deprecated use <code>getTextBlockProperties</code>
	 */
	@Deprecated
	public Float getRowSpacing() {
		return textBlockProps.getRowSpacing();
	}

	
	public TextBorderStyle getTextBorderStyle() {
		return textBorderStyle;
	}
	
	/**
	 * Gets the minimum number of lines of a paragraph that must be
	 * left at the bottom of a page. 
	 * @return returns the orphans
	 */
	public int getOrphans() {
		return orphans;
	}

	/**
	 * Gets the minimum number of lines of a paragraph that must be
	 * left at the top of a page.
	 * @return returns the widows
	 */
	public int getWidows() {
		return widows;
	}
	
	public TextBlockProperties getTextBlockProperties() {
		return textBlockProps;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		//shallow copy should be fine, as all member variables are either immutable or primitives
		return super.clone();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + blockIndent;
		result = prime * result + ((breakBefore == null) ? 0 : breakBefore.hashCode());
		result = prime * result + ((keep == null) ? 0 : keep.hashCode());
		result = prime * result + keepWithNext;
		result = prime * result + keepWithNextSheets;
		result = prime * result + keepWithPreviousSheets;
		result = prime * result + ((listType == null) ? 0 : listType.hashCode());
		result = prime * result + ((margin == null) ? 0 : margin.hashCode());
		result = prime * result + orphans;
		result = prime * result + ((padding == null) ? 0 : padding.hashCode());
		result = prime * result + ((textBlockProps == null) ? 0 : textBlockProps.hashCode());
		result = prime * result + ((textBorderStyle == null) ? 0 : textBorderStyle.hashCode());
		result = prime * result + ((verticalPosition == null) ? 0 : verticalPosition.hashCode());
		result = prime * result + widows;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BlockProperties other = (BlockProperties) obj;
		if (blockIndent != other.blockIndent) {
			return false;
		}
		if (breakBefore != other.breakBefore) {
			return false;
		}
		if (keep != other.keep) {
			return false;
		}
		if (keepWithNext != other.keepWithNext) {
			return false;
		}
		if (keepWithNextSheets != other.keepWithNextSheets) {
			return false;
		}
		if (keepWithPreviousSheets != other.keepWithPreviousSheets) {
			return false;
		}
		if (listType != other.listType) {
			return false;
		}
		if (margin == null) {
			if (other.margin != null) {
				return false;
			}
		} else if (!margin.equals(other.margin)) {
			return false;
		}
		if (orphans != other.orphans) {
			return false;
		}
		if (padding == null) {
			if (other.padding != null) {
				return false;
			}
		} else if (!padding.equals(other.padding)) {
			return false;
		}
		if (textBlockProps == null) {
			if (other.textBlockProps != null) {
				return false;
			}
		} else if (!textBlockProps.equals(other.textBlockProps)) {
			return false;
		}
		if (textBorderStyle == null) {
			if (other.textBorderStyle != null) {
				return false;
			}
		} else if (!textBorderStyle.equals(other.textBorderStyle)) {
			return false;
		}
		if (verticalPosition == null) {
			if (other.verticalPosition != null) {
				return false;
			}
		} else if (!verticalPosition.equals(other.verticalPosition)) {
			return false;
		}
		if (widows != other.widows) {
			return false;
		}
		return true;
	}

}