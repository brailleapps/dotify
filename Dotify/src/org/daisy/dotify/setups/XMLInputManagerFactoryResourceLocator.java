package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

public class XMLInputManagerFactoryResourceLocator extends
		AbstractResourceLocator {

	public XMLInputManagerFactoryResourceLocator() {
		super();
	}

	public XMLInputManagerFactoryResourceLocator(String basePath) {
		super(basePath);
	}

	public URL getCatalogResourceURL() throws ResourceLocatorException {
		return getResource("catalog.xml");
	}
}
