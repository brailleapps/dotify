package org.daisy.dotify.setups;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.system.InputManager;
import org.daisy.dotify.system.InputManagerFactory;
import org.daisy.dotify.text.FilterLocale;

public class XMLInputManagerFactory implements InputManagerFactory {
	private final Logger logger;
	private final Properties tables = new Properties();
	
	public XMLInputManagerFactory() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		try {
	        URL tablesURL = new XMLInputManagerFactoryResourceLocator().getCatalogResourceURL();
	        if(tablesURL!=null){
	        	tables.loadFromXML(tablesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate catalog file");
	        }
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load catalog.", e);
		}
	}

	public boolean supportsLocale(FilterLocale locale) {
		return tables.getProperty(locale.toString())!=null;
	}
	
	public Set<String> listSupportedLocales() {
		HashSet<String> ret = new HashSet<String>();
		for (Object key : tables.keySet()) {
			ret.add(key.toString());
		}
		return ret;
	}

	public InputManager newInputManager(FilterLocale locale) {
        String languageFileRelativePath = tables.getProperty(locale.toString());
        if(languageFileRelativePath==null) {
        	throw new IllegalArgumentException("Locale not supported: " + locale.toString());
        } else {
        	return new XMLInputManager(
        			new XMLInputManagerFactoryResourceLocator(languageFileRelativePath),
        			new CommonResourceLocator());
        }
	}

}
