package org.daisy.dotify.system;


/**
 * A TaskSystemFactoryException is an exception that indicates 
 * conditions in a {@link TaskSystemFactoryMaker} that a reasonable 
 * application might want to catch.
 * @author Joel Håkansson
 *
 */
public class TaskSystemFactoryException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7053028902035757516L;

	public TaskSystemFactoryException() { }

	public TaskSystemFactoryException(String message) { super(message); }

	public TaskSystemFactoryException(Throwable cause) { super(cause); }

	public TaskSystemFactoryException(String message, Throwable cause) { super(message, cause); }

}
