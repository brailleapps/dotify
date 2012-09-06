package org.daisy.dotify.system;

import java.net.URL;

public interface ConfigurationManager {
	
	public URL getConfigurationURL(String identifier) throws ResourceLocatorException;
	
}
