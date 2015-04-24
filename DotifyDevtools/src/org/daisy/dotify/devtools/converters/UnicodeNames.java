package org.daisy.dotify.devtools.converters;

import java.io.IOException;
import java.util.HashMap;

import org.daisy.dotify.common.text.TextFileReader;
import org.daisy.dotify.common.text.TextFileReader.LineData;

public class UnicodeNames {
	private enum UnicodeData {
		INSTANCE;
		private final HashMap<Integer, String> map;
		
		private UnicodeData() {
			map = new HashMap<Integer,String>();
			TextFileReader tfr = new TextFileReader.Builder(this.getClass().getResourceAsStream("resource-files/UnicodeData.txt")).regex(";").build();
			LineData l;
			try {
				while ((l = tfr.nextLine())!=null) {
					map.put(Integer.parseInt(l.getFields()[0], 16), l.getFields()[1]);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		private String getName(int codepoint) {
			return map.get(codepoint);
		}
	}

	/**
	 * Gets the name of the character with the specified code point.
	 * @param codepoint the code point
	 */
    public static String getName(int codepoint) {
    	return UnicodeData.INSTANCE.getName(codepoint);
    }

}