package org.daisy.dotify.system;

import java.util.ArrayList;



/**
 * TaskSystem is an interface used when compiling a series of InternalTasks
 * that may require both global Transformer parameters and individual
 * constructor arguments.
 * 
 * Implement this interface to create a new TaskSystem.
 * @author Joel Håkansson, TPB
 *
 */
public interface TaskSystem {
	
	/**
	 * Get a descriptive name for the TaskSystem
	 * @return returns the name for the TaskSystem
	 */
	public String getName();

	/**
	 * Compile the TaskSystem using the supplied parameters
	 * @param parameters the parameters to pass to the TaskSystem
	 * @return returns a list of InternalTasks
	 * @throws TaskSystemException throws TaskSystemException if something goes wrong when compiling the TaskSystem
	 */
	public ArrayList<InternalTask> compile(RunParameters parameters) throws TaskSystemException;

}
