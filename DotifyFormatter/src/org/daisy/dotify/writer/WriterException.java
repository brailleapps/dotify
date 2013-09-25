/**
 * 
 */
package org.daisy.dotify.writer;

/**
 * @author joha
 *
 */
public class WriterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2552962274053684229L;

	/**
	 * 
	 */
	public WriterException() {
	}

	/**
	 * @param message
	 */
	public WriterException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public WriterException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public WriterException(String message, Throwable cause) {
		super(message, cause);
	}

}
