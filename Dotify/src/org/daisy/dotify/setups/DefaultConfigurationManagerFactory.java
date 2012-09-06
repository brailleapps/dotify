package org.daisy.dotify.setups;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.system.ConfigurationManager;
import org.daisy.dotify.system.ConfigurationManagerFactory;
import org.daisy.dotify.text.FilterLocale;

public class DefaultConfigurationManagerFactory implements ConfigurationManagerFactory {
	private final Logger logger;
	private final Properties props = new Properties();
	
	public DefaultConfigurationManagerFactory() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		try {
	        URL tablesURL = new XMLInputManagerFactoryResourceLocator().getCatalogResourceURL();
	        if(tablesURL!=null){
	        	props.loadFromXML(tablesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate catalog file");
	        }
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load catalog.", e);
		}
	}

	public boolean supportsLocale(FilterLocale locale) {
		return props.getProperty(locale.toString())!=null;
	}
	
	public Set<String> listSupportedLocales() {
		HashSet<String> ret = new HashSet<String>();
		for (Object key : props.keySet()) {
			ret.add(key.toString());
		}
		return ret;
	}

	public ConfigurationManager newConfigurationManager(FilterLocale locale) {
        String languageFileRelativePath = props.getProperty(locale.toString());
        if(languageFileRelativePath==null) {
        	throw new IllegalArgumentException("Locale not supported: " + locale.toString());
        } else {
        	return new DefaultConfigurationManager(
        			new XMLInputManagerFactoryResourceLocator(languageFileRelativePath),
        			new CommonResourceLocator());
        }
	}

}
