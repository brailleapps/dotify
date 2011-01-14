package org.daisy.dotify.system.tasks.layout.flow;

/**
 * A LayoutException is an exception that indicates 
 * conditions in the layout process that a reasonable 
 * application might want to catch.
 * @author Joel Håkansson, TPB
 */
public class LayoutException extends Exception {

	static final long serialVersionUID = -2908554164728732775L;

	/**
	 * Constructs a new exception with null as its detail message.
	 */
	public LayoutException() { super(); }

	/**
	 * Constructs a new exception with the specified detail message.
	 * @param message the detail message
	 */
	public LayoutException(String message) { super(message); }

	/**
	 * Constructs a new exception with the specified cause
	 * @param cause the cause
	 */
	public LayoutException(Throwable cause) { super(cause); }

	/**
	 * Constructs a new exception with the specified detail message and cause.
	 * @param message the detail message
	 * @param cause the cause
	 */
	public LayoutException(String message, Throwable cause) { super(message, cause); }

}
