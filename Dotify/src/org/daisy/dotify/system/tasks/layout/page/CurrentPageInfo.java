package org.daisy.dotify.system.tasks.layout.page;

/**
 * Contains information about the current page
 * @author Joel Håkansson, TPB
 *
 */
public interface CurrentPageInfo {

	/**
	 * Gets the flow height for the current page
	 * @return returns the flow height of the current page
	 */
	public int getFlowHeight();
	
	/**
	 * Gets the number of rows currently on the current page
	 * @return returns the number of rows in the current page
	 */
	public int countRows();

}
