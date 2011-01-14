package org.daisy.dotify.setups;

import java.io.IOException;

public class ResourceLocatorException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6379009305571540260L;

	public ResourceLocatorException() {
		super();
	}

	public ResourceLocatorException(String message) {
		super(message);
	}

	public ResourceLocatorException(Throwable cause) {
		super();
		//Java 1.5 does not support 'super(cause)'
		super.initCause(cause);
	}

	public ResourceLocatorException(String message, Throwable cause) {
		super(message);
		//Java 1.5 does not support 'super(message, cause)'
		super.initCause(cause);
	}

}
