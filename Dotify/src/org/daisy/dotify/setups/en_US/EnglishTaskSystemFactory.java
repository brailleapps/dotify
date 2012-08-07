package org.daisy.dotify.setups.en_US;

import org.daisy.dotify.SystemKeys;
import org.daisy.dotify.system.TaskSystem;
import org.daisy.dotify.system.TaskSystemFactory;
import org.daisy.dotify.system.TaskSystemFactoryException;
import org.daisy.dotify.text.FilterLocale;

public class EnglishTaskSystemFactory implements TaskSystemFactory {

	public boolean supportsSpecification(FilterLocale locale, String outputFormat) {
		return locale.equals(FilterLocale.parse("en-US")) && SystemKeys.TEXT_FORMAT.equals(outputFormat);
	}

	public TaskSystem newTaskSystem(FilterLocale locale, String outputFormat) throws TaskSystemFactoryException {
		if (locale.equals(FilterLocale.parse("en-US"))) {
			if (SystemKeys.TEXT_FORMAT.equals(outputFormat)) {
				return new DefaultTextSystem("DefaultTextSystem");
			}			
		}
		throw new TaskSystemFactoryException("Unsupported specification: " + locale + "/" + outputFormat);
	}

}
