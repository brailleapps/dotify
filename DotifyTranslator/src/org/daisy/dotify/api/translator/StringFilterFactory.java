package org.daisy.dotify.api.translator;



/**
 * FilterFactory is an interface for creating a StringFilter for a specified FilterLocale.
 * @author Joel Håkansson
 */
public interface StringFilterFactory {
	
	/**
	 * Get a new StringFilter for the specified FilterLocale
	 * @param target the FilterLocale for the StringFilter
	 * @return returns a new StringFilter for the specified FilterLocale
	 * @throws throws IllegalArgumentException, if no filter is found
	 */
	public StringFilter newStringFilter(String target);


}
