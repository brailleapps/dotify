package org.daisy.dotify.impl.config;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.common.io.AbstractResourceLocator;
import org.daisy.dotify.common.io.ResourceLocatorException;
import org.daisy.dotify.config.ConfigurationsProvider;

/**
 * Provides a default set of configurations. These should not be accessed directly,
 * use the ConfigurationsCatalog instead. 
 * @author Joel Håkansson
 */
public class DefaultConfigurationsProvider extends AbstractResourceLocator implements ConfigurationsProvider {
	private final static String PRESETS_PATH = "resource-files/";
	private final Logger logger;
	private final Properties props = new Properties();
	private Properties descs = null;
	private final Map<String, String> urls;
	
	/**
	 * Creates a new default configurations provider. This should not be accessed directly,
	 * use the ConfigurationsCatalog instead.
	 */
	public DefaultConfigurationsProvider() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		try {
	        URL tablesURL = getResource("presets_catalog.xml");
	        if(tablesURL!=null){
	        	props.loadFromXML(tablesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate catalog file");
	        }
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load catalog.", e);
		}
		urls = new HashMap<String, String>();
		for (Entry<Object, Object> e : props.entrySet()) {
			urls.put(e.getKey().toString(), PRESETS_PATH + e.getValue());
		}
	}

	public Set<String> getConfigurationKeys() {
		return urls.keySet();
	}

	private URL getConfigurationURL(String identifier) throws ResourceLocatorException {
		return this.getResource(urls.get(identifier));
	}

	public Properties getConfiguration(String identifier) throws ResourceLocatorException {
		Properties p = new Properties();
		URL configURL = getConfigurationURL(identifier);
		try {
			p.loadFromXML(configURL.openStream());
		} catch (FileNotFoundException e) {
			throw new ResourceLocatorException("Configuration file not found: " + configURL, e);
		} catch (InvalidPropertiesFormatException e) {
			throw new ResourceLocatorException("Configuration file could not be parsed: " + configURL, e);
		} catch (IOException e) {
			throw new ResourceLocatorException("IOException while reading configuration file: " + configURL, e);
		}
		return p;
	}

	public String getConfigurationDescription(String identifier) {
		if (descs == null) {
			descs = new Properties();
			try {
				URL url = getResource("presets_descriptions.xml");
				descs.loadFromXML(url.openStream());
			} catch (ResourceLocatorException e) {
				logger.log(Level.FINE, "Problem reading catalog descriptions", e);
			} catch (InvalidPropertiesFormatException e) {
				logger.log(Level.FINE, "Problem reading catalog descriptions", e);
			} catch (IOException e) {
				logger.log(Level.FINE, "Problem reading catalog descriptions", e);
			}
			
		}
		return descs.getProperty(identifier);
	}

}