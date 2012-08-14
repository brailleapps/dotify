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
	
	URL getConfigurationCatalogResourceURL() throws ResourceLocatorException {
		return getResource("presets_catalog.xml");
	}
	
	URL getInputFormatCatalogResourceURL() throws ResourceLocatorException {
		return getResource("input_format_catalog.xml");
	}

}
