package org.daisy.dotify.formatter.dom;



public class NumeralField {
	public static enum NumeralStyle {
		/**
		 * The field should be formatted 
		 */
		DEFAULT,
		/**
		 * The field should be formatted as a Roman numeral
		 */
		ROMAN};

	private NumeralStyle style;
	
	public NumeralField(NumeralStyle style) {
		this.style = style;
	}
	
	public NumeralStyle getStyle() {
		return style;
	}
	
	public String style(int i) {
		switch (style) {
			case ROMAN:
				return RomanNumeral.int2roman(i);
			case DEFAULT:default:
				return "" + i;
		}
	}

}
