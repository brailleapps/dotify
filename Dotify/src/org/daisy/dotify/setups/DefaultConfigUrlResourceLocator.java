package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

public class DefaultConfigUrlResourceLocator extends AbstractResourceLocator {

	public DefaultConfigUrlResourceLocator() {
		super();
	}

	public DefaultConfigUrlResourceLocator(String basePath) {
		super(basePath);
	}
	
	public URL getCatalogResourceURL() throws ResourceLocatorException {
		return getResource("configuration_catalog.xml");
	}

}
