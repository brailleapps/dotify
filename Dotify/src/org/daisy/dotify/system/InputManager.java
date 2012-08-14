package org.daisy.dotify.system;

import java.net.URL;


public interface InputManager extends TaskSystem {

	public URL getConfigurationURL(String identifier) throws ResourceLocatorException;
}
