package org.daisy.dotify.impl.input;

import java.io.IOException;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;

import org.daisy.dotify.tools.AbstractResourceLocator;
import org.daisy.dotify.tools.ResourceLocatorException;

class DefaultInputUrlResourceLocator extends AbstractResourceLocator {
	private static DefaultInputUrlResourceLocator instance;
	private Properties props;

	private DefaultInputUrlResourceLocator() {
		super();
		props = null;
	}

	synchronized static DefaultInputUrlResourceLocator getInstance() {
		if (instance==null) {
			instance = new DefaultInputUrlResourceLocator();
		}
		return instance;
	}
	
	private synchronized void loadIfNotLoaded() throws ResourceLocatorException {
		if (props==null) {
			props = new Properties();
			try {
				props.loadFromXML(getResource("input_format_catalog.xml").openStream());
			} catch (InvalidPropertiesFormatException e) {
				throw new ResourceLocatorException();
			} catch (IOException e) {
				throw new ResourceLocatorException();
			}
		}
	}

	Properties getInputFormatCatalog() throws ResourceLocatorException {
		loadIfNotLoaded();
		return props;
	}
	
	String getConfigFileName(String rootElement, String rootNS) throws ResourceLocatorException {
		loadIfNotLoaded();
		if (rootNS!=null) {
			return props.getProperty(rootElement+"@"+rootNS);
		} else {
			return props.getProperty(rootElement);
		}
	}

}
