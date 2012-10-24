package org.daisy.dotify.impl.system.common;

import java.net.URL;

import org.daisy.dotify.tools.AbstractResourceLocator;
import org.daisy.dotify.tools.ResourceLocatorException;

/**
 * Provides a method to find resources relative to this class 
 * @author Joel Håkansson
 *
 */
public class CommonResourceLocator extends AbstractResourceLocator {
	/**
	 * Provides identifiers that can be used to locate resources
	 * maintained by this class.
	 */
	public enum CommonResourceIdentifier {
		/**
		 * An XSLT that adds meta data from a DTBook source to a PEF-file input.
		 * The URI to the DTBook should be in a parameter named 'input-uri'
		 */
		META_FINALIZER_XSLT
	}
	private static CommonResourceLocator instance;
	
	private CommonResourceLocator() {
		super();
	}
	
	/**
	 * Gets the instance of the Obfl resource locator if it exists, or creates
	 * it if it does not (singleton).
	 * @return returns the instance
	 */
	public static synchronized CommonResourceLocator getInstance() {
		if (instance==null) {
			instance = new CommonResourceLocator();
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
	public URL getResourceByIdentifier(CommonResourceIdentifier identifier) {
		try {
			switch (identifier) {
				case META_FINALIZER_XSLT:
					return getResource("resource-files/meta-finalizer.xsl");
				default:
					throw new RuntimeException("Enum identifier not implemented. This is a coding error.");
			}
		} catch (ResourceLocatorException e) {
			throw new RuntimeException("Could not locate resource by enum identifier. This is a coding error.", e);
		}
		
	}
}
