package org.daisy.dotify.devtools.converters;

public class CodePointHelper {
	public enum Style {
		XML,
		COMMA
	}
	public enum Mode {
		HEX,
		DECIMAL
	}
	public enum Input {
		NAME, CODE
	}

	/**
	 * Formats a number as a zero padded hex string of a specified length.
	 * @param i the number to format
	 * @param len the length of the resulting string
	 * @return returns a string of the specified length
	 */
	public static String toHexString(int i, int len) {
		return padStr(Integer.toHexString(i), len, '0');
	}
	
	/**
	 * Parses a string as code point entities. For example:
	 * "0065,0066,0067" will be return "ABC". 
	 * @param str
	 * @param st
	 * @param m
	 * @return
	 */
   public static String parse(String str, Mode m) {
    	if (str==null || str.equals("")) {
    		return "";
    	}
    	String[]strs;
    	Style st = (str.contains("&")?Style.XML:Style.COMMA);
    	switch (st) {
    		case XML:
    			strs = str.split(";");
    			break;
    		case COMMA: default:
    			strs = str.split(",");
    			break;
    	}
    	StringBuffer ret = new StringBuffer();
    	for (String s : strs) {
    		s = s.trim();
    		switch (st) {
    			case XML:
    				switch (m) {
	    				case DECIMAL:
	        				if (s.startsWith("&#")) {
	        					s = s.substring(2);
	        				} else {
	        					throw new IllegalArgumentException("Cannot parse string");
	        				}
	    					break;
	    				case HEX: default:
	        				if (s.startsWith("&#x")) {
	        					s = s.substring(3);
	        				} else {
	        					throw new IllegalArgumentException("Cannot parse string");
	        				}
	    					break;
    				}
    				break;
    			case COMMA: default:
    				break;
    		}
    		switch (m) {
    		case DECIMAL:
    			ret.append((char)Integer.parseInt(s, 10));
    			break;
    		case HEX: default:
    			ret.append((char)Integer.parseInt(s, 16));
    			break;
    		}
    	}
    	return ret.toString();
    }
    
   /**
    * Formats a string as code point entities. 
    * @param str
    * @param s
    * @param m
    * @return
    */
	public static String format(String str, Style s, Mode m) {
		StringBuffer sb = new StringBuffer();
		
		for (int i=0; i<str.length(); i++) {
			if (s==Style.XML) {
				switch (m) {
					case DECIMAL:
						sb.append("&#");
						break;
					case HEX: default:
						sb.append("&#x");
						break;
				}
			}
			switch (m) {
				case DECIMAL:
					sb.append(Integer.toString(str.codePointAt(i)));
					break;
				case HEX: default:
					sb.append(CodePointHelper.toHexString(str.codePointAt(i), 4));
					break;
			}
			switch (s) {
				case XML:
					sb.append(";");
					break;
				case COMMA:
					if (i<str.length()-1) {
						sb.append(", ");
					}
					break;
			}
		}
		return sb.toString();
	}

	private static String padStr(String in, int len, char padding) {
		StringBuilder sb = new StringBuilder();
		for (int i=in.length(); i<len; i++) {
			sb.append(padding);
		}
		sb.append(in);
		return sb.toString();
	}
	
}