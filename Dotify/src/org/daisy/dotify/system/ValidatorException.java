package org.daisy.dotify.system;

public class ValidatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5418099071057870838L;

	public ValidatorException() {
	}

	public ValidatorException(String message) {
		super(message);
	}

	public ValidatorException(Throwable cause) {
		super(cause);
	}

	public ValidatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
