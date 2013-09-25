package org.daisy.dotify.formatter;


/**
 * Provides a numeral field.
 * 
 * @author Joel Håkansson
 */
public class NumeralField implements Field {
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
		ROMAN;

		/**
		 * Formats the numeral with the given style
		 * 
		 * @param i
		 *            the number
		 * @return returns the formatted number
		 */
		public String format(int i) {
			switch (this) {
				case ROMAN:
					return RomanNumeral.int2roman(i);
				case DEFAULT:
				default:
					return "" + i;
			}
		}
	};

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


}
