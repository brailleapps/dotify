package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

class DefaultConfigUrlResourceLocator extends AbstractResourceLocator {

	DefaultConfigUrlResourceLocator() {
		super();
	}

	DefaultConfigUrlResourceLocator(String basePath) {
		super(basePath);
	}
	
	URL getCatalogResourceURL() throws ResourceLocatorException {
		return getResource("configuration_catalog.xml");
	}

}
