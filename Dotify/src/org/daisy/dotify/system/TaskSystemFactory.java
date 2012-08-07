package org.daisy.dotify.system;

import org.daisy.dotify.text.FilterLocale;

public interface TaskSystemFactory {
	
	/**
	 * Returns true if this factory can create instances for the specified locale.
	 * @param locale
	 * @return
	 */
	public boolean supportsSpecification(FilterLocale locale, String outputFormat);
	

	public TaskSystem newTaskSystem(FilterLocale locale, String outputFormat) throws TaskSystemFactoryException;
	
}
