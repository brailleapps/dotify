package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.translator.TextBorderStyle;
import org.daisy.dotify.tools.StringTools;

/**
 * Provides a way to add a border to a set of paragraphs.
 * @author Joel Håkansson
 */
class TextBorder {
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
	private final int topFill, rowFill, bottomFill, width;
	private final String topLeftCorner, topBorder, topRightCorner, leftBorder,
			rightBorder, bottomLeftCorner, bottomBorder, bottomRightCorner,
			fillCharacter;

	//private Align align;

	private final List<RowImpl> ret;

	/**
	 * The Builder is used when creating a TextBorder instance.
	 * @author Joel Håkansson
	 */
	public static class Builder {
		final int width;
		final String fillCharacter;
		//Align align;
		TextBorderStyle style;
		String outerLeftMargin, innerLeftMargin, innerRightMargin;
		
		/**
		 * Creates a new Builder
		 * @param width the width of the block including borders
		 */
		public Builder(int width, String fillCharacter) {
			this.width = width;
			this.fillCharacter = fillCharacter;
			//this.align = Align.LEFT;
			this.style = TextBorderStyle.NONE;
			this.outerLeftMargin = "";
			this.innerLeftMargin = "";
			this.innerRightMargin = "";
		}
		
		/**
		 * Sets the text border style
		 * 
		 * @param style
		 *            the style
		 * @return returns this Builder
		 */
		public Builder style(TextBorderStyle style) {
			this.style = style;
			return this;
		}

		/**
		 * Sets the text alignment
		 * @param align the text alignment
		 * @return returns this Builder
		 *//*
		public Builder alignment(Align align) {
			this.align = align;
			return this;
		}*/
		
		public Builder outerLeftMargin(String margin) {
			this.outerLeftMargin = margin;
			return this;
		}

		public Builder innerLeftMargin(String margin) {
			this.innerLeftMargin = margin;
			return this;
		}

		public Builder innerRightMargin(String margin) {
			this.innerRightMargin = margin;
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
		//this.align = builder.align;

		this.topLeftCorner = builder.outerLeftMargin + builder.style.getTopLeftCorner();
		this.topBorder = builder.style.getTopBorder();
		this.topRightCorner = builder.style.getTopRightCorner();
		this.leftBorder = builder.outerLeftMargin + builder.style.getLeftBorder() + builder.innerLeftMargin;
		this.rightBorder = builder.innerRightMargin + builder.style.getRightBorder();
		this.bottomLeftCorner = builder.outerLeftMargin + builder.style.getBottomLeftCorner();
		this.bottomBorder = builder.style.getBottomBorder();
		this.bottomRightCorner = builder.style.getBottomRightCorner();

		this.topFill = builder.width - (topLeftCorner.length() + topRightCorner.length());
		this.rowFill = builder.width - (leftBorder.length() + rightBorder.length());
		this.bottomFill = builder.width - (bottomLeftCorner.length() + bottomRightCorner.length());
		this.width = builder.width;
		this.fillCharacter = builder.fillCharacter;
		this.ret = new ArrayList<RowImpl>();
	}

	/**
	 * Gets the rendered top border
	 * @return returns the rendered top border 
	 */
	public String getTopBorder() {
		return topLeftCorner + StringTools.fill(topBorder, topFill) + topRightCorner;
	}

	/**
	 * Gets the rendered bottom border
	 * @return returns the rendered bottom border
	 */
	public String getBottomBorder() {
		return bottomLeftCorner + StringTools.fill(bottomBorder, bottomFill) + bottomRightCorner;
	}

	/**
	 * <p>Adds borders to a paragraph of text. Each row is padded to
	 * fill up unused space and surrounded by the left and right border patterns.</p>
	 * <p>If the text does not fit within a row, the text is broken and the process 
	 * is continued on a new row.</p>
	 * @param bph the translator result to add borders to
	 * @return returns an ArrayList of String where each String is a row in the block.
	 *//*
	public void addParagraph(BrailleTranslatorResult bph) {
		for (String s : addBorderToParagraph(bph)) {
			ret.add(new RowImpl(s));
		}
	}

	public void addParagraph(BrailleTranslatorResult bph, int fill) {
		ArrayList<String> vol = addBorderToParagraph(bph);
		while (ret.size() <= fill - vol.size() - 3) {
			addRow("");
		}
		for (String s : vol) {
			ret.add(new RowImpl(s));
		}
	}

	private ArrayList<String> addBorderToParagraph(BrailleTranslatorResult bph) {
		ArrayList<String> ret = new ArrayList<String>();
    	while (bph.hasNext()) {
			// .replaceAll("\\s*\\z", "") is probably not needed, as
			// nextTranslatedRow must not exceed the row length
			ret.add(addBorderToRow(bph.nextTranslatedRow(rowFill, true).replaceAll("\\s*\\z", ""), align, "", "", false));
    	}
    	return ret;
	}*/

	/**
	 * Adds borders to a line of text.
	 * @param text the text to add borders to
	 * @return returns the text padded with space and surrounded with the left and right border patterns.
	 * @throws IllegalArgumentException if the String does not fit within a single row.
	 */
	/*
	public RowImpl addRow(String text) {
		RowImpl row = new RowImpl(addBorderToRow(text, align, "", "", false));
		ret.add(row);
		return row;
	}*/
	
	public int getRowFill() {
		return rowFill;
	}

	public String addBorderToRow(String text, String innerRightBorder, boolean bypass) {
    	StringBuffer sb = new StringBuffer();
		sb.append(leftBorder);
    	sb.append(text);
		if (!bypass) {
			//pad to size
			sb.append(StringTools.fill(fillCharacter, width - sb.length() - innerRightBorder.length() - rightBorder.length()));
			sb.append(innerRightBorder);
			sb.append(rightBorder);
    	}
    	return sb.toString();
	}

	public List<RowImpl> getResult() {
		ArrayList<RowImpl> result = new ArrayList<RowImpl>();
		result.add(new RowImpl(getTopBorder()));
		result.addAll(ret);
		ret.clear();
		result.add(new RowImpl(getBottomBorder()));
		return result;
	}

	public List<String> getStringResult() {
		ArrayList<String> result = new ArrayList<String>();
		result.add(getTopBorder());
		// TODO: remove row from this class, unless really needed...
		for (RowImpl r : ret) {
			result.add(r.getChars());
		}
		ret.clear();
		result.add(getBottomBorder());
		return result;
	}

}
