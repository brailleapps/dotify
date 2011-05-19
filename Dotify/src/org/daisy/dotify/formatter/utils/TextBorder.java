package org.daisy.dotify.formatter.utils;

import java.util.ArrayList;

/**
 * Provides a way to add a border to a set of paragraphs.
 * @author Joel Håkansson, TPB
 */
public class TextBorder {
	/**
	 * Text alignment within the bordered box
	 */
	public enum Align {
		/**
		 * Align text to the left
		 */
		LEFT, 
		/**
		 * Center text 
		 */
		CENTER, 
		/**
		 * Align text to the right
		 */
		RIGHT};
	private int topFill, rowFill, bottomFill;
	private String 	topLeftCorner, topBorder, topRightCorner, 
					leftBorder, rightBorder,
					bottomLeftCorner, bottomBorder, bottomRightCorner;
	private Align align;

	/**
	 * The Builder is used when creating a TextBorder instance.
	 * @author Joel Håkansson, TPB
	 */
	public static class Builder {
		int width;
		String 	topLeftCorner, topBorder, topRightCorner, 
				leftBorder, rightBorder,
				bottomLeftCorner, bottomBorder, bottomRightCorner;
		Align align;
		
		/**
		 * Creates a new Builder
		 * @param width the width of the block including borders
		 */
		public Builder(int width) {
			this.width = width;
			this.topLeftCorner = "";
			this.topBorder = "";
			this.topRightCorner = "";
			this.leftBorder = "";
			this.rightBorder = "";
			this.bottomLeftCorner = "";
			this.bottomBorder = "";
			this.bottomRightCorner = "";
			this.align = Align.LEFT;
		}
		
		/**
		 * Sets the top left corner for the border
		 * @param pattern top left corner pattern 
		 * @return returns this Builder
		 */
		public Builder topLeftCorner(String pattern) {
			this.topLeftCorner = pattern;
			return this;
		}

		/**
		 * Sets the top of the border
		 * @param pattern the top border pattern
		 * @return returns this Builder
		 */
		public Builder topBorder(String pattern) {
			this.topBorder = pattern;
			return this;
		}
		
		/**
		 * Sets the top right corner for the border
		 * @param pattern top right corner pattern
		 * @return returns this Builder
		 */
		public Builder topRightCorner(String pattern) {
			this.topRightCorner = pattern;
			return this;
		}
		
		/**
		 * Sets the left border pattern
		 * @param pattern the left border pattern
		 * @return returns this Builder
		 */
		public Builder leftBorder(String pattern) {
			this.leftBorder = pattern;
			return this;
		}
		
		/**
		 * Sets the right border pattern
		 * @param pattern the right border pattern
		 * @return returns this Builder
		 */
		public Builder rightBorder(String pattern) {
			this.rightBorder = pattern;
			return this;
		}
		
		/**
		 * Sets the bottom left corner
		 * @param pattern bottom left corner pattern
		 * @return returns this Builder
		 */
		public Builder bottomLeftCorner(String pattern) {
			this.bottomLeftCorner = pattern;
			return this;
		}

		/**
		 * Sets the bottom of the border
		 * @param pattern bottom border pattern
		 * @return returns this Builder
		 */
		public Builder bottomBorder(String pattern) {
			this.bottomBorder = pattern;
			return this;
		}
		
		/**
		 * Sets the bottom right corner
		 * @param pattern bottom right corner pattern
		 * @return returns this Builder
		 */
		public Builder bottomRightCorner(String pattern) {
			this.bottomRightCorner = pattern;
			return this;
		}
		
		/**
		 * Sets the text alignment
		 * @param align the text alignment
		 * @return returns this Builder
		 */
		public Builder alignment(Align align) {
			this.align = align;
			return this;
		}
		
		/**
		 * Build TextBorder using the current state of the Builder
		 * @return returns a new TextBorder instance
		 */
		public TextBorder build() {
			return new TextBorder(this);
		}
	}

	private TextBorder(Builder builder) {
		this.topLeftCorner = builder.topLeftCorner;
		this.topBorder = builder.topBorder;
		this.topRightCorner = builder.topRightCorner;
		this.leftBorder = builder.leftBorder;
		this.rightBorder = builder.rightBorder;
		this.bottomLeftCorner = builder.bottomLeftCorner;
		this.bottomBorder = builder.bottomBorder;
		this.bottomRightCorner = builder.bottomRightCorner;
		this.align = builder.align;
		this.topFill = builder.width - (topLeftCorner.length() + topRightCorner.length());
		this.rowFill = builder.width - (leftBorder.length() + rightBorder.length());
		this.bottomFill = builder.width - (bottomLeftCorner.length() + bottomRightCorner.length());
	}

	/**
	 * Gets the rendered top border
	 * @return returns the rendered top border 
	 */
	public String getTopBorder() {
		return topLeftCorner + LayoutTools.fill(topBorder, topFill) + topRightCorner;
	}

	/**
	 * Gets the rendered bottom border
	 * @return returns the rendered bottom border
	 */
	public String getBottomBorder() { 
		return bottomLeftCorner + LayoutTools.fill(bottomBorder, bottomFill) + bottomRightCorner;
	}

	/**
	 * <p>Adds borders to a paragraph of text. Each row is padded to
	 * fill up unused space and surrounded by the left and right border patterns.</p>
	 * <p>If the text does not fit within a row, the text is broken and the process 
	 * is continued on a new row.</p>
	 * @param text the text to add borders to
	 * @return returns an ArrayList of String where each String is a row in the block.
	 */
	public ArrayList<String> addBorderToParagraph(String text) {
		ArrayList<String> ret = new ArrayList<String>();
		BreakPointHandler bph = new BreakPointHandler(text);
    	BreakPoint bp;
    	while (bph.hasNext()) {
    		bp = bph.nextRow(rowFill);
    		ret.add(addBorderToRow(bp.getHead().replaceAll("\\s*\\z", "")));
    	}
    	return ret;
	}

	/**
	 * Adds borders to a line of text.
	 * @param text the text to add borders to
	 * @return returns the text padded with space and surrounded with the left and right border patterns.
	 * @throws IllegalArgumentException if the String does not fit within a single row.
	 */
	public String addBorderToRow(String text) {
    	if (text.length()>rowFill) {
    		throw new IllegalArgumentException("String length must be <= width");
    	}
    	StringBuffer sb = new StringBuffer();
    	sb.append(leftBorder);
    	switch (align) {
	    	case LEFT: break;
	    	case CENTER:
	    		sb.append(LayoutTools.fill(' ', (int)Math.floor( (rowFill - text.length())/2d) ));
	    		break;
	    	case RIGHT:
	    		sb.append(LayoutTools.fill(' ', rowFill - text.length()));
	    		break;
    	}
    	sb.append(text);
    	switch (align) {
	    	case LEFT:
	    		sb.append(LayoutTools.fill(' ', rowFill - text.length()));
	    		break;
	    	case CENTER:
	    		sb.append(LayoutTools.fill(' ', (int)Math.ceil( (rowFill - text.length())/2d) ));
	    		break;
	    	case RIGHT: break;
    	}
    	sb.append(rightBorder);
    	return sb.toString();
	}

}
