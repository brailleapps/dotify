package org.daisy.dotify.config;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;

public class DefaultConfigurationsProvider extends AbstractResourceLocator implements ConfigurationsProvider {
	private final static String PRESETS_PATH = "resource-files/";
	private final Logger logger;
	private final Properties props = new Properties();
	private final Map<String, String> urls;
	
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
		for (Object key : props.keySet()) {
			urls.put(key.toString(), PRESETS_PATH + props.getProperty((String)key));
		}
	}

	public Set<String> getConfigurationKeys() {
		return urls.keySet();
	}

	public URL getConfigurationURL(String identifier) throws ResourceLocatorException {
		return this.getResource(urls.get(identifier));
	}

}