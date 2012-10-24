package org.daisy.dotify.impl.system.sv_SE;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemFactory;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.text.FilterLocale;

public class SwedishTaskSystemFactory implements TaskSystemFactory {

	public boolean supportsSpecification(FilterLocale locale, String outputFormat) {
		return locale.equals(FilterLocale.parse("sv-SE")) && 
				(SystemKeys.PEF_FORMAT.equals(outputFormat) || SystemKeys.OBFL_FORMAT.equals(outputFormat));
	}

	public TaskSystem newTaskSystem(FilterLocale locale, String outputFormat) throws TaskSystemFactoryException {
		if (locale.equals(FilterLocale.parse("sv-SE"))) {
			if (SystemKeys.PEF_FORMAT.equals(outputFormat) || SystemKeys.OBFL_FORMAT.equals(outputFormat)) {
				return new SwedishBrailleSystem("SwedishBrailleSystem", outputFormat, locale);
			}
		}
		throw new TaskSystemFactoryException("Unsupported specification: " + locale + "/" + outputFormat);
	}

}
