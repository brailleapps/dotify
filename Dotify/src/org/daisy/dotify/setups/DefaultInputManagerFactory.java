package org.daisy.dotify.setups;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.input.InputManager;
import org.daisy.dotify.input.InputManagerFactory;
import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.text.FilterLocale;

public class DefaultInputManagerFactory implements InputManagerFactory {
	private final Logger logger;
	private final Properties tables = new Properties();
	
	public DefaultInputManagerFactory() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		try {
	        URL tablesURL = new DefaultInputManagerFactoryResourceLocator().getCatalogResourceURL();
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
        	return new InputManager(
        			new DefaultInputManagerFactoryResourceLocator(languageFileRelativePath),
        			new CommonResourceLocator());
        }
	}

}
