package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.text.FilterLocale;

/**
 * Provides localized parameters.
 * @author Joel Håkansson
 */
public class LocalizationManager {
	private final LocalizationResourceLocator locator;
	
	/**
	 * Creates a new instance.
	 */
	public LocalizationManager() {
		this.locator = new LocalizationResourceLocator();
	}

	/**
	 * Gets a URL containing localized parameters for the specified locale.
	 * @param locale the locale to get localized parameters for
	 * @return returns a URL containing localized parameters
	 * @throws ResourceLocatorException throws ResourceLocatorException if no localized
	 * parameters can be found for the specified locale.
	 */
	public URL getLocalizationUrl(FilterLocale locale) throws ResourceLocatorException {
		return locator.getResourceLocator(locale).getResource("localization.xml");
	}

}
