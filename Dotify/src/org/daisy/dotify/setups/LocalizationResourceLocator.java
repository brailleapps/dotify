package org.daisy.dotify.setups;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.text.FilterLocale;

public class LocalizationResourceLocator extends AbstractResourceLocator {

	private final Properties tables;
	
	public LocalizationResourceLocator() {
		super();
		Logger logger = Logger.getLogger(this.getClass().getCanonicalName());
		tables = new Properties();
		try {
	        URL tablesURL = getLocalizationCatalogURL();
	        if(tablesURL!=null){
	        	tables.loadFromXML(tablesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate catalog file");
	        }
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load catalog.", e);
		}
	}

	private LocalizationResourceLocator(String basePath) {
		super(basePath);
		tables = new Properties();
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
	
	public URL getLocalizationCatalogURL() throws ResourceLocatorException {
		return getResource("localization_catalog.xml");
	}
	
	/**
	 * Gets a resource locator for the given locale.
	 * @param locale
	 * @return
	 */
	public LocalizationResourceLocator getResourceLocator(FilterLocale locale) {
		String languageFileRelativePath = tables.getProperty(locale.toString());
        if(languageFileRelativePath==null) {
        	throw new IllegalArgumentException("Locale not supported: " + locale.toString());
        } else {
        	return new LocalizationResourceLocator(languageFileRelativePath);
        }
	}
}
