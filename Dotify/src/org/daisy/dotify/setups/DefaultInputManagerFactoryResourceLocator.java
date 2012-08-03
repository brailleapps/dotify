package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

public class DefaultInputManagerFactoryResourceLocator extends
		AbstractResourceLocator {

	public DefaultInputManagerFactoryResourceLocator() {
		super();
	}

	public DefaultInputManagerFactoryResourceLocator(String basePath) {
		super(basePath);
	}

	public URL getCatalogResourceURL() throws ResourceLocatorException {
		return getResource("catalog.xml");
	}
}
