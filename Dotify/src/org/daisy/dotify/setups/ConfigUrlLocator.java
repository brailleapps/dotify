package org.daisy.dotify.setups;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.system.TaskSystemFactoryException;

public class ConfigUrlLocator {
	private final Logger logger;
	private final Properties tables = new Properties();
	
	public ConfigUrlLocator() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		try {
	        URL tablesURL = new DefaultConfigUrlResourceLocator().getConfigurationCatalogResourceURL();
	        if(tablesURL!=null){
	        	tables.loadFromXML(tablesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate catalog file");
	        }
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load catalog.", e);
		}
	}
	
	public Set<Object> getKeys() {
		return tables.keySet();
	}
	
	public String getSubpath(String identifier) {
		return tables.getProperty(identifier);
	}
	
	public URL getResourceURL(String identifier) throws TaskSystemFactoryException {
        String path = tables.getProperty(identifier);
        if(path==null) {
        	// try identifier as path
        	try {
        		return new URL(identifier);
        	} catch (MalformedURLException e) {
        		throw new IllegalArgumentException("Cannot find configuration for " + identifier);
        	}
        } else {
        	try {
				return new DefaultConfigUrlResourceLocator().getResource(path);
			} catch (ResourceLocatorException e) {
				throw new TaskSystemFactoryException("Failed to locate resource.", e);
			}
        }

	}
}