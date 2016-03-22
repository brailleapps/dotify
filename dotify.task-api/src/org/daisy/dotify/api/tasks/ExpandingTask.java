package org.daisy.dotify.api.tasks;

import java.io.File;

/**
 * Provides an abstract base for expanding tasks. 
 * 
 * @author Joel Håkansson
 *
 */
public abstract class ExpandingTask extends InternalTask { //NOPMD

	/**
	 * Creates a new expanding task with the specified name
	 * @param name the name of the task
	 */
	public ExpandingTask(String name) {
		super(name);
	}

	/**
	 * Resolves the task into other tasks based on the contents of the <code>input</code>.
	 * @param input input file
	 * @throws InternalTaskException throws InternalTaskException if something goes wrong.
	 */
	public abstract InternalTaskGroup resolve(File input) throws InternalTaskException;

}
