package org.daisy.dotify.text;

import org.daisy.dotify.hyphenator.api.HyphenatorConfigurationException;
import org.daisy.dotify.hyphenator.api.HyphenatorInterface;
import org.daisy.dotify.hyphenator.spi.HyphenatorFactoryMaker;

/**
 * Provides a hyphenating string filter. This filter will hyphenate the
 * filter input using the supplied hyphenator.
 * 
 * @author Joel Håkansson
 *
 */
public class HyphenatorFilter implements StringFilter {
	private final HyphenatorInterface hyphenator;

	public HyphenatorFilter(FilterLocale locale) throws HyphenatorConfigurationException {
		this(HyphenatorFactoryMaker.newInstance().newHyphenator(locale));
	}
	
	public HyphenatorFilter(HyphenatorInterface hyphenator) {
		this.hyphenator = hyphenator;
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