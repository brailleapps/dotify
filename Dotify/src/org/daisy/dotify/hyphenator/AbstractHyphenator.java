package org.daisy.dotify.hyphenator;


public abstract class AbstractHyphenator implements HyphenatorInterface {
	
	protected int beginLimit = 2, endLimit = 2; 

	public int getBeginLimit() {
		return beginLimit;
	}

	public void setBeginLimit(int beginLimit) {
		this.beginLimit = beginLimit;
	}

	public int getEndLimit() {
		return endLimit;
	}

	public void setEndLimit(int endLimit) {
		this.endLimit = endLimit;
	}

}
