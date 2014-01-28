package org.daisy.dotify.obfl;

import java.util.Map;



/**
 * Provides an interface for block contents.
 * @author Joel Håkansson
 *
 */
interface BlockContents extends IterableEventContents {

	/**
	 * Sets the evaluate context using the supplied map where <tt>key</tt>
	 * is a variable name and <tt>value</tt> is the variables value. 
	 * @param vars a map containing variables and their value
	 */
	public void setEvaluateContext(Map<String, String> vars);
}
