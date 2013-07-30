package org.daisy.dotify.translator.attributes;

/**
 * Provides an interface to a marker processor. A marker processor is
 * responsible for converting and inserting the supplied
 * text attributes as text markers.
 * 
 * @author Joel Håkansson
 */
public interface MarkerProcessor {

	/**
	 * Processes the input text and attributes into a text containing
	 * markers at the appropriate positions. The length of the text
	 * must match the text attributes specified width.
	 * 
	 * @param text
	 *            the text to process
	 * @param atts
	 *            the text attributes that apply to the text.
	 * @return returns string with markers
	 * @throws IllegalArgumentException
	 *             if the specified attributes does not
	 *             match the text.
	 */
	public String process(String text, TextAttribute atts);

}
