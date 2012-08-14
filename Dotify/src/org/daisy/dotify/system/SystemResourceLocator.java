package org.daisy.dotify.system;

import java.net.URL;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

/**
 * Provides a method to find resources relative to this class 
 * @author Joel Håkansson
 *
 */
public class SystemResourceLocator extends AbstractResourceLocator {
	public enum SystemResourceIdentifier {
		OBFL_XML_SCHEMA;
	}
	private static SystemResourceLocator instance;
	
	private SystemResourceLocator() {
		super();
	}
	
	public static synchronized SystemResourceLocator getInstance() {
		if (instance==null) {
			instance = new SystemResourceLocator();
		}
		return instance;
	}
	
	/**
	 * Gets a resource by identifier. It is preferred to use this method 
	 * rather than get a resource by string, since the internal structure
	 * of this package should be considered opaque to users of this class.
	 * @param identifier the identifier of the resource to get.
	 * @return returns the URL to the resource
	 */
	public URL getResourceByIdentifier(SystemResourceIdentifier identifier) {
		try {
			switch (identifier) {
				case OBFL_XML_SCHEMA: return getResource("resource-files/flow.xsd");
				default: return null;
			}
		} catch (ResourceLocatorException e) {
			throw new RuntimeException("Could not locate resource by enum identifier. This is a coding error.", e);
		}
	}

}
