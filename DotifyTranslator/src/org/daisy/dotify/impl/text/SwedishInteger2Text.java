package org.daisy.dotify.impl.text;

import org.daisy.dotify.text.Integer2Text;
import org.daisy.dotify.text.IntegerOutOfRange;

class SwedishInteger2Text implements Integer2Text {

	public String intToText(int value) throws IntegerOutOfRange {
		if (value < 0) return "minus " + intToText(-value);
		switch (value) {
			case 0:
				return "noll";
			case 1:
				return "ett";
			case 2:
				return "två";
			case 3:
				return "tre";
			case 4:
				return "fyra";
			case 5:
				return "fem";
			case 6:
				return "sex";
			case 7:
				return "sju";
			case 8:
				return "åtta";
			case 9:
				return "nio";
			case 10:
				return "tio";
			case 11:
				return "elva";
			case 12:
				return "tolv";
			case 13:
				return "tretton";
			case 14:
				return "fjorton";
			case 15:
				return "femton";
			case 16:
				return "sexton";
			case 17:
				return "sjutton";
			case 18:
				return "arton";
			case 19:
				return "nitton";
			case 20:
				return "tjugo";
			case 30:
				return "trettio";
			case 40:
				return "fyrtio";
			case 50:
				return "femtio";
			case 60:
				return "sextio";
			case 70:
				return "sjuttio";
			case 80:
				return "åttio";
			case 90:
				return "nittio";
		}
		String pre = "";
		if (value >= 1000) {
			pre = intToText(value / 1000) + "tusen";
			value = value % 1000;
		}
		if (value >= 100) {
			pre = pre + (value >= 200 ? intToText(value / 100) : "") + "hundra";
			value = value % 100;
		}
		// replace three occurrences of the same character by two
		pre = pre.replaceAll("(\\w)(\\1{2})", "$2");
		if (value == 0) return pre;
		if (value < 20) {
			return pre + intToText(value);
		} else {
			int t = value % 10;
			int r = (value / 10) * 10;
			return pre + intToText(r) + (t > 0 ? intToText(t) : "");
		}
	}

}
