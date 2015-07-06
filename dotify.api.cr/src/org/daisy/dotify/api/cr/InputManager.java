package org.daisy.dotify.api.cr;

import java.util.List;
import java.util.Map;


/**
 * An input manager is a file/fileset conversion unit. An implementation
 * should provide a useful small conversion step that may be used in a
 * larger context, for example to assemble a task system based on
 * several such implementations. However, an input manager MUST NOT
 * depend on other input managers to serve it's purpose.
 * 
 * @author Joel Håkansson
 */
public interface InputManager {
	
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
	public List<InternalTask> compile(Map<String, Object> parameters) throws TaskSystemException;

}
