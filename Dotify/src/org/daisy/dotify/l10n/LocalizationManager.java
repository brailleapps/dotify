package org.daisy.dotify.l10n;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.InvalidPropertiesFormatException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.tools.ResourceLocatorException;

/**
 * Provides localized parameters.
 * @author Joel Håkansson
 */
public class LocalizationManager {
	private final LocalizationResourceLocator locator;
	private final Map<FilterLocale, Properties> propsCatalog;
	
	/**
	 * Creates a new instance.
	 */
	public LocalizationManager() {
		this.locator = new LocalizationResourceLocator();
		this.propsCatalog = new HashMap<FilterLocale, Properties>();
	}


	private URL getLocalizationUrl(FilterLocale locale) throws ResourceLocatorException {
		return locator.getResourceLocator(locale).getResource("localization.xml");
	}
	
	/**
	 * Gets properties containing localized parameters for the specified locale.
	 * @param locale the locale to get localized parameters for
	 * @return returns localized parameters
	 */
	public Properties getContentLocalization(FilterLocale locale) {
		Properties props = propsCatalog.get(locale);
		if (props==null) {
			props = new Properties();
			try {
				props.loadFromXML(getLocalizationUrl(locale).openStream());
				propsCatalog.put(locale, props);
			} catch (InvalidPropertiesFormatException e) {
				logger().log(Level.FINE, "", e);
			} catch (ResourceLocatorException e) {
				logger().log(Level.FINE, "", e);
			} catch (IOException e) {
				logger().log(Level.FINE, "", e);
			}
		}
		return props;
	}
	
	private Logger logger() {
		return Logger.getLogger(this.getClass().getCanonicalName());
	}

}
