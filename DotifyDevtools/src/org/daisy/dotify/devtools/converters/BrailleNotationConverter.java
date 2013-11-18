package org.daisy.dotify.devtools.converters;

public class BrailleNotationConverter {
	
	//TODO: Add as test
	/* 
	public static void main(String[] args) {
		String input = "p12456p1p4p0p12345678p2p3p4p5p6p7p8p1";
		System.out.println(input + " -> " + charsToUnicode(input));
	}*/
	
	public static String parsePNotation(String p) {
		String[] s = p.split("p");
		if (s.length == 0) {
			throw new IllegalArgumentException("Illegal sequence");
		}
		StringBuffer sb = new StringBuffer();
		for (String t : s) {
			if (!t.equals("")) {
				sb.append(pStringToUnicode(t));
			}
		}
		return sb.toString();
	}
	
	private static char pStringToUnicode(String p) {
		int v = 0;
		int prv = 0;
		char prvC = (char) 0;
		for (char c : p.toCharArray()) {
			if (prvC > c) {
				throw new IllegalArgumentException("Illegal format");
			} else {
				prvC = c;
			}
			switch (c) {
				case '0': v |= 0x2800; break;
				case '1': v |= 0x2801; break;
				case '2': v |= 0x2802; break;
				case '3': v |= 0x2804; break;
				case '4': v |= 0x2808; break;
				case '5': v |= 0x2810; break;
				case '6': v |= 0x2820; break;
				case '7': v |= 0x2840; break;
				case '8': v |= 0x2880; break;
				default:
					throw new IllegalArgumentException("Illegal character: " + c);
			}
			if (v == prv) {
				throw new IllegalArgumentException("Illegal format");
			} else {
				prv = v;
			}
		}
		return (char)v;
		/*
		String ret = Integer.toHexString(v);
		if (ret.length()<2) { ret = "0" + ret;}
		ret = "&#x28" + ret + ";";
		return ret;*/
	}

}
