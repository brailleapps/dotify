package org.daisy.dotify.hyphenator;

public class FeatureNotSupportedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7784339351020337103L;

	public FeatureNotSupportedException() {
		super();
	}

	public FeatureNotSupportedException(String message) {
		super(message);
	}

	public FeatureNotSupportedException(Throwable cause) {
		super(cause);
	}

	public FeatureNotSupportedException(String message, Throwable cause) {
		super(message, cause);
	}

}
