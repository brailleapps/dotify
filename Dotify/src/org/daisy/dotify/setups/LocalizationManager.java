package org.daisy.dotify.setups;

import java.net.URL;

import org.daisy.dotify.system.ResourceLocatorException;
import org.daisy.dotify.text.FilterLocale;

public class LocalizationManager {
	private final LocalizationResourceLocator locator;
	
	public LocalizationManager() {
		this.locator = new LocalizationResourceLocator();
	}

	public URL getLocalizationUrl(FilterLocale locale) throws ResourceLocatorException {
		LocalizationResourceLocator res = locator.getResourceLocator(locale);
		return res.getResource("localization.xml");
	}

}
