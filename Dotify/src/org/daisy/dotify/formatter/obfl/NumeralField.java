package org.daisy.dotify.formatter.obfl;

import org.daisy.dotify.formatter.utils.RomanNumeral;

/**
 * Provides a numeral field.
 * 
 * @author Joel Håkansson
 */
public class NumeralField {
	/**
	 * Defines numeral styles
	 */
	public static enum NumeralStyle {
		/**
		 * Defines default numeral style
		 */
		DEFAULT,
		/**
		 * Defines roman numeral style
		 */
		ROMAN};

	private NumeralStyle style;
	
	/**
	 * Creates a new numeral with the supplied style.
	 * @param style the style for this numeral
	 */
	public NumeralField(NumeralStyle style) {
		this.style = style;
	}
	
	/**
	 * Gets the style for this numeral.
	 * @return the style for this numeral
	 */
	public NumeralStyle getStyle() {
		return style;
	}
	
	/**
	 * Formats the numeral with the given style
	 * @param i the style
	 * @return returns the numeral formatted using the supplied style
	 */
	public String style(int i) {
		switch (style) {
			case ROMAN:
				return RomanNumeral.int2roman(i);
			case DEFAULT:default:
				return "" + i;
		}
	}

}
