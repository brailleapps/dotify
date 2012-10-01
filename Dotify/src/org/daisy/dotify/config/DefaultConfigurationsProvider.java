package org.daisy.dotify.config;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

/**
 * Provides a default set of configurations. These should not be accessed directly,
 * use the ConfigurationsCatalog instead. 
 * @author Joel Håkansson
 */
public class DefaultConfigurationsProvider extends AbstractResourceLocator implements ConfigurationsProvider {
	private final static String PRESETS_PATH = "resource-files/";
	private final Logger logger;
	private final Properties props = new Properties();
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

	public URL getConfigurationURL(String identifier) throws ResourceLocatorException {
		return this.getResource(urls.get(identifier));
	}

}