package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

class DefaultInputUrlResourceLocator extends AbstractResourceLocator {

	DefaultInputUrlResourceLocator() {
		super();
	}

	DefaultInputUrlResourceLocator(String basePath) {
		super(basePath);
	}
	
	URL getInputFormatCatalogResourceURL() throws ResourceLocatorException {
		return getResource("input_format_catalog.xml");
	}

}
