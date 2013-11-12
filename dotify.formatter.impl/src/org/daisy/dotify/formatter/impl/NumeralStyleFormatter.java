package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.api.formatter.NumeralField;

class NumeralStyleFormatter {

	/**
	 * Formats the numeral with the given style
	 * 
	 * @param i
	 *            the number
	 * @return returns the formatted number
	 */
	public static String format(int i, NumeralField.NumeralStyle style) {
		switch (style) {
			case ROMAN:
			case UPPER_ROMAN:
				return RomanNumeral.int2roman(i);
			case LOWER_ROMAN:
				return RomanNumeral.int2roman(i).toLowerCase();
			case ALPHA:
			case UPPER_ALPHA:
				return AlphaNumeral.int2alpha(i);
			case LOWER_ALPHA:
				return AlphaNumeral.int2alpha(i).toLowerCase();
			case DEFAULT:
			default:
				return "" + i;
		}
	}
}
