package org.daisy.dotify.text;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import com.ibm.icu.text.Normalizer;
import com.ibm.icu.text.UCharacterIterator;

/**
 * <p>
 * Provides substitution for unicode characters with replacement strings.
 * </p>
 * 
 * <p>
 * This is a much simplified version of UCharReplacer by Markus Gylling from the
 * org.daisy.util package.
 * </p>
 * 
 * <p>
 * The use of this class <em>may</em> result in a change in unicode character
 * composition between input and output. If you need a certain normalization
 * form, normalize after the use of this class.
 * </p>
 * 
 * <p>
 * Usage example:
 * </p>
 * <code><pre>
 * SimpleCharReplacer ucr = new SimpleCharReplacer();
 * ucr.addSubstitutionTable(fileURL);
 * ucr.addSubstitutionTable(fileURL2);
 * String ret = ucr.replace(input);
 * </pre></code>
 * 
 * <p>
 * The translation table file is using the same xml format as that of
 * java.util.Properties [1][2], using the HEX representation (without the
 * characteristic 0x-prefix!) of a unicode character as the <tt>key</tt>
 * attribute and the replacement string as value of the <tt>entry</tt> element.
 * </p>
 * 
 * <p>
 * If the <tt>key</tt> attribute contains exactly one unicode codepoint (one
 * character) it will be treated literally. It will not be interpreted as a HEX
 * representation of another character, even if theoretically possible. E.g. if
 * the <tt>key</tt> is "a", it will be treated as 0x0061 rather than as 0x000a
 * </p>
 * 
 * <p>
 * Note - there is a significant difference between a unicode codepoint (32 bit
 * int) and a UTF16 codeunit (=char) - a codepoint consists of one or two
 * codeunits.
 * </p>
 * <p>
 * To make sure an int represents a codepoint and not a codeunit, use for
 * example <code>com.ibm.icu.text.Normalizer</code> to NFC compose, followed by
 * <code>com.ibm.icu.text.UCharacterIterator</code> to retrieve possibly non-BMP
 * codepoints from a string.
 * </p>
 * 
 * @see [1] http://java.sun.com/j2se/1.5.0/docs/api/java/util/Properties.html
 * @see [2] http://java.sun.com/dtd/properties.dtd
 * 
 * @author Joel Håkansson
 * @author Markus Gylling (UCharReplacer)
 */
public class SimpleCharReplacer {
	private Map<Integer, String> mSubstitutionTable = null;

	public SimpleCharReplacer() {
		mSubstitutionTable = new HashMap<Integer, String>();
	}

	public void addSubstitutionTable(URL table) throws IOException {
		try {
			loadTable(table);
		} catch (Exception e) {
			throw new IOException(e.getMessage());
		}
	}

	public CharSequence replace(String input) {
		int codePoint;

		StringBuilder sb = new StringBuilder(input.length());

		// icu4j version
		// normalize to eliminate any ambiguities vis-a-vis the user tables
		Normalizer.normalize(input, Normalizer.NFC);

		// Java 1.6 SDK version
		// Normalizer.normalize(input, Normalizer.Form.NFC);

		// icu4j version
		// iterate over each code point in the input string
		UCharacterIterator uci = UCharacterIterator.getInstance(input.toString());
		while ((codePoint = uci.nextCodePoint()) != UCharacterIterator.DONE) {
			CharSequence substitution = substitute(codePoint);
			if (null != substitution && substitution.length() > 0) {
				// a replacement occurred
				sb.append(substitution);
			} else {
				// a replacement didn't occur
				sb.appendCodePoint(codePoint);
			}
		}

		/*
		 * Java 1.5 SDK version
		 * // iterate over each code point in the input string
		 * final int length = input.length();
		 * for (int offset = 0; offset < length;) {
		 * codePoint = input.codePointAt(offset);
		 * CharSequence substitution = substitute(codePoint);
		 * if (null != substitution && substitution.length() > 0) {
		 * // a replacement occurred
		 * sb.append(substitution);
		 * } else {
		 * // a replacement didn't occur
		 * sb.appendCodePoint(codePoint);
		 * }
		 * offset += Character.charCount(codePoint);
		 * }
		 */

		return sb;
	}

	/**
	 * Loads a table using the Properties class.
	 */
	private void loadTable(URL tableURL) throws IOException {
		Properties props = new Properties();
		props.loadFromXML(tableURL.openStream());
		Set<?> keys = props.keySet();
		for (Iterator<?> it = keys.iterator(); it.hasNext();) {
			String key = (String) it.next();
			if (key.codePointCount(0, key.length()) == 1) {
				mSubstitutionTable.put(key.codePointAt(0), props.getProperty(key));
			} else {
				try {
					mSubstitutionTable.put(Integer.decode("0x" + key), props.getProperty(key));
				} catch (NumberFormatException e) {
					System.err.println("error in translation table " + tableURL.toString() + ": attribute key=\"" + key + "\" is not a hex number.");
				}
			}
		}
	}

	/**
	 * @return a substite string if available in tables, or null if not
	 *         available
	 */
	private String substitute(int codePoint) {
		return mSubstitutionTable.get(Integer.valueOf(codePoint));
	}

}
