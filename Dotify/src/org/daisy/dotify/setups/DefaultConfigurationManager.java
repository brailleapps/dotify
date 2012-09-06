package org.daisy.dotify.setups;

import java.net.MalformedURLException;
import java.net.URL;

import org.daisy.dotify.system.ConfigurationManager;
import org.daisy.dotify.system.ResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

class DefaultConfigurationManager implements ConfigurationManager {
	private final static String PRESETS_PATH = "presets/";
	private final ResourceLocator localLocator;
	private final ResourceLocator commonLocator;
	
	DefaultConfigurationManager(ResourceLocator localLocator, ResourceLocator commonLocator) {
		this.localLocator = localLocator;
		this.commonLocator = commonLocator;
	}

	public URL getConfigurationURL(String identifier) throws ResourceLocatorException {
		ConfigUrlLocator c = new ConfigUrlLocator();
		String subPath = c.getSubpath(identifier);
		if (subPath==null) {
        	// try identifier as path
        	try {
        		return new URL(identifier);
        	} catch (MalformedURLException e) {
        		throw new IllegalArgumentException("Cannot find configuration for " + identifier);
        	}
		} else {
			try {
				return localLocator.getResource(PRESETS_PATH + subPath);
			} catch (ResourceLocatorException e) {
				// try common locator
				
			}
			return commonLocator.getResource(PRESETS_PATH + subPath);
		}
	}

}
