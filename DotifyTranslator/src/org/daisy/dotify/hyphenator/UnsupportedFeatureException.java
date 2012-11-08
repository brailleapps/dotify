package org.daisy.dotify.hyphenator;

/**
 * Provides an exception that indicates that a feature is not supported.
 * 
 * @author Joel Håkansson
 *
 */
public class UnsupportedFeatureException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7784339351020337103L;

	public UnsupportedFeatureException() {
		super();
	}

	public UnsupportedFeatureException(String message) {
		super(message);
	}

	public UnsupportedFeatureException(Throwable cause) {
		super(cause);
	}

	public UnsupportedFeatureException(String message, Throwable cause) {
		super(message, cause);
	}

}
