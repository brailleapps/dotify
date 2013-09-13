package org.daisy.dotify.api.translator;

public class TranslationException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1834392372293939932L;

	protected TranslationException() {
		super();
	}

	protected TranslationException(String message, Throwable cause) {
		super(message, cause);
	}

	protected TranslationException(String message) {
		super(message);
	}

	protected TranslationException(Throwable cause) {
		super(cause);
	}

}
