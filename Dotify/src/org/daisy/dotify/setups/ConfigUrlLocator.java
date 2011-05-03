package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.setups.TaskSystemFactory.OutputFormat;
import org.daisy.dotify.setups.TaskSystemFactory.Setup;
import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

public class ConfigUrlLocator extends AbstractResourceLocator {
	
	public URL getResourceURL(OutputFormat outputFormat, Setup setup) throws TaskSystemFactoryException {
		try {
			switch (outputFormat) {
				case PEF:
					switch (setup) {
						// Braille setups for Swedish //
						case sv_SE: 
							return getResource("sv_SE/config/default_A4.xml");
						case sv_SE_FA44:
							return getResource("sv_SE/config/default_FA44.xml");
						// Add more Braille systems here //
					}
					break;
				case TEXT:
					switch (setup) {
						// Text setup for Swedish //
						case sv_SE: 
							return getResource("sv_SE/config/text_A4.xml");
						case en_US:
							return getResource("en_US/config/text.xml");
						// Add more text systems here //
					}
					break;
			}
		} catch (ResourceLocatorException e) {
			throw new TaskSystemFactoryException("Failed to locate resource.", e);
		}
		throw new TaskSystemFactoryException("Cannot find configuration for " + outputFormat + "/" + setup);
	}

}
