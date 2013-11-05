package org.daisy.dotify.impl.text;

import org.daisy.dotify.api.text.Integer2Text;
import org.daisy.dotify.api.text.IntegerOutOfRange;

class BasicInteger2Text implements Integer2Text {
	private BasicInt2TextLocalization loc;

	BasicInteger2Text(BasicInt2TextLocalization loc) {
		this.loc = loc;
	}

	public String intToText(int value) throws IntegerOutOfRange {
		return loc.postProcess(intToTextInner(value));
	}

	private String intToTextInner(int value) throws IntegerOutOfRange {
		if (value >= 10000) {
			throw new IntegerOutOfRange();
		} else if (value < 0) {
			return loc.formatNegative(intToTextInner(-value));
		} else {
			try {
				return loc.getDefinedValue(value);
			} catch (UndefinedNumberException e) {
				// no defined value, try to divide...
			}
			if (value >= 1000) {
				int rem = value % 1000;
				return loc.formatThousands(intToTextInner(value / 1000), (rem > 0 ? intToTextInner(rem) : ""));
			} else if (value >= 100) {
				int rem = value % 100;
				return loc.formatHundreds(intToTextInner(value / 100), (rem > 0 ? intToTextInner(rem) : ""));
			} else if (value >= 20) {
				int t = value % 10;
				int r = (value / 10) * 10;
				return loc.formatTens(intToTextInner(r), (t > 0 ? intToTextInner(t) : ""));
			} else {
				throw new RuntimeException();
			}
		}
	}

}
