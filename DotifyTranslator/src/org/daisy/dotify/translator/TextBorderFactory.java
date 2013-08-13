package org.daisy.dotify.translator;

import java.util.Set;
import java.util.logging.Logger;

import org.daisy.dotify.text.TextBorderStyle;

public class TextBorderFactory {

	private TextBorderFactory() {

	}

	public static TextBorderFactory newInstance() {
		return new TextBorderFactory();
	}

	public TextBorderStyle newTextBorderStyle(String mode, Set<String> set) {
		if (!mode.equals(BrailleTranslatorFactory.MODE_BYPASS)) {

			// this is pretty stupid
			if (set.contains("solid")) {
				if (set.contains("wide")) {
					if (set.contains("inner")) {
						return BrailleTextBorderStyle.SOLID_WIDE_INNER;
					} else if (set.contains("outer")) {
						return BrailleTextBorderStyle.SOLID_WIDE_OUTER;
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).fine("Ignoring unknown frame " + set);
					}
				} else if (set.contains("thin")) {
					if (set.contains("inner")) {
						return BrailleTextBorderStyle.SOLID_THIN_INNER;
					} else if (set.contains("outer")) {
						return BrailleTextBorderStyle.SOLID_THIN_OUTER;
					} else {
						Logger.getLogger(this.getClass().getCanonicalName()).fine("Ignoring unknown frame " + set);
					}
				} else {
					Logger.getLogger(this.getClass().getCanonicalName()).fine("Ignoring unknown frame " + set);
				}
			}
		}
		return null;
	}

}
