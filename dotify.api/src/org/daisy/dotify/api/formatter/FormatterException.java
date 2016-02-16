package org.daisy.dotify.api.formatter;

public class FormatterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3200550160814809972L;

	public FormatterException() {
	}

	public FormatterException(String message) {
		super(message);
	}

	public FormatterException(Throwable cause) {
		super(cause);
	}

	public FormatterException(String message, Throwable cause) {
		super(message, cause);
	}

	public FormatterException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
