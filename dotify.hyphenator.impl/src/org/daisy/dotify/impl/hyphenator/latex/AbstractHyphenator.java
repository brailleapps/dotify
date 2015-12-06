package org.daisy.dotify.impl.hyphenator.latex;

import org.daisy.dotify.api.hyphenator.HyphenatorInterface;

/**
 * Provides an abstract base for hyphenators. The abstract implementation
 * provides getters and setters for simple properties of the hyphenator
 * interface, reducing the amount of code in the concrete implementation
 * with a few lines.
 * @author Joel Håkansson
 *
 */
public abstract class AbstractHyphenator implements HyphenatorInterface {
	protected int beginLimit = 2, endLimit = 2; 

	@Override
	public int getBeginLimit() {
		return beginLimit;
	}

	@Override
	public void setBeginLimit(int beginLimit) {
		this.beginLimit = beginLimit;
	}

	@Override
	public int getEndLimit() {
		return endLimit;
	}

	@Override
	public void setEndLimit(int endLimit) {
		this.endLimit = endLimit;
	}

}
