package org.daisy.dotify.paginator;

public class PaginatorException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8015133306865945283L;

	public PaginatorException() {
	}

	public PaginatorException(String message) {
		super(message);
	}

	public PaginatorException(Throwable cause) {
		super(cause);
	}

	public PaginatorException(String message, Throwable cause) {
		super(message, cause);
	}

}
