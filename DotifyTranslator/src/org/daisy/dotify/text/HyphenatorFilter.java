package org.daisy.dotify.text;

import org.daisy.dotify.hyphenator.HyphenatorFactoryMaker;
import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;

public class HyphenatorFilter implements StringFilter {
	private final HyphenatorInterface hyphenator;

	public HyphenatorFilter(FilterLocale locale) throws UnsupportedLocaleException {
		hyphenator = HyphenatorFactoryMaker.newInstance().newHyphenator(locale);
	}

	public int getBeginLimit() {
		return hyphenator.getBeginLimit();
	}

	public void setBeginLimit(int beginLimit) {
		hyphenator.setBeginLimit(beginLimit);
	}

	public int getEndLimit() {
		return hyphenator.getEndLimit();
	}

	public void setEndLimit(int endLimit) {
		hyphenator.setEndLimit(endLimit);
	}
	
	public String filter(String str) {
		return hyphenator.hyphenate(str);
	}
	
}