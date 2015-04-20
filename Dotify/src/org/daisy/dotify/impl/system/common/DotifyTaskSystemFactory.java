package org.daisy.dotify.impl.system.common;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemFactory;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a default task system factory for PEF, OBFL and text output.
 * 
 * @author Joel Håkansson
 */
public class DotifyTaskSystemFactory implements TaskSystemFactory {

	public boolean supportsSpecification(FilterLocale locale, String outputFormat) {
		//TODO: remove conditions guard once possible 
		return locale.equals(FilterLocale.parse("sv-SE")) && 
				(SystemKeys.PEF_FORMAT.equals(outputFormat) || SystemKeys.OBFL_FORMAT.equals(outputFormat))
				|| SystemKeys.TEXT_FORMAT.equals(outputFormat);
	}

	public TaskSystem newTaskSystem(FilterLocale locale, String outputFormat) throws TaskSystemFactoryException {
		if (supportsSpecification(locale, outputFormat)) {
			return new DotifyTaskSystem("Dotify Task System", outputFormat, locale);
		}
		throw new TaskSystemFactoryException("Unsupported specification: " + locale + "/" + outputFormat);
	}

}
