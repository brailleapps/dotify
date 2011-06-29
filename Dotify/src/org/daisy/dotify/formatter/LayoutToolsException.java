package org.daisy.dotify.formatter;

/**
 * A LayoutToolsException is an exception that indicates 
 * conditions in a LayoutTools process that a reasonable 
 * application might want to catch.
 * @author Joel Håkansson, TPB
 */
public class LayoutToolsException extends Exception {

	static final long serialVersionUID = 2207586942417976960L;

	/**
	 * Constructs a new exception with null as its detail message.
	 */
	public LayoutToolsException() { super(); }

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message
	 */
	public LayoutToolsException(String message) { super(message); }

	/**
	 * Constructs a new exception with the specified cause
	 * @param cause the cause
	 */
	public LayoutToolsException(Throwable cause) { super(cause); }

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * @param message the detail message
	 * @param cause the cause
	 */
	public LayoutToolsException(String message, Throwable cause) { super(message, cause); }

}
