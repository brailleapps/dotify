package org.daisy.dotify.impl.text;

interface BasicInt2TextLocalization {

	/**
	 * Gets a lookup value value.
	 * 
	 * @param value
	 *            the value to look up
	 * @return returns the string for the lookup value
	 * @throws UndefinedNumberException
	 *             if the value cannot is not defined
	 */
	String getDefinedValue(int value) throws UndefinedNumberException;

	/**
	 * Formats the value as negative. The value will be intToText(-x)
	 * 
	 * @param value
	 *            the number as text
	 * @return returns the formatted string
	 */
	String formatNegative(String value);

	/**
	 * Formats thousands. E.g. if the value is 9700 the first argument
	 * will be intToText(9) and the second will be intToText(700).
	 * 
	 * @param thousands
	 *            the thousands as text
	 * @param rem
	 *            the remainder as text
	 * 
	 * @return returns the formatted string
	 */
	String formatThousands(String thousands, String rem);

	/**
	 * Formats hundreds. E.g. if the value is 120 the first argument
	 * will be intToText(1) and the second will be intToText(20).
	 * 
	 * @param hundreds
	 *            the hundreds as text
	 * @param rem
	 *            the remainder as text
	 * @return returns the formatted string
	 */
	String formatHundreds(String hundreds, String rem);

	/**
	 * Formats tens. E.g. if the value is 38 the first argument
	 * will be intToText(30) and the second will be intToText(8).
	 * 
	 * @param tens
	 *            the tens as text
	 * @param rem
	 *            the remainder as text
	 * @return returns the formatted string
	 */
	String formatTens(String tens, String rem);

	/**
	 * Applies post processing to the processed result
	 * 
	 * @param value
	 *            the conversion result
	 * @return returns the processed value
	 */
	String postProcess(String value);
}
