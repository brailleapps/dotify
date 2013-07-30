package org.daisy.dotify.translator.attributes;


/**
 * Provides an interface for marker dictionaries. A
 * marker dictionary resolves text markers based on the
 * contents of the supplied string.
 * 
 * @author Joel Håkansson
 * 
 */
public interface MarkerDictionary {

	/**
	 * Gets the markers that apply to the specified string.
	 * 
	 * @param str
	 *            the string to find markers for
	 * @return returns markers that apply to the input
	 * @throws MarkerNotFoundException
	 *             if no markers apply to the supplied string
	 */
	public Marker getMarkersFor(String str) throws MarkerNotFoundException;

}