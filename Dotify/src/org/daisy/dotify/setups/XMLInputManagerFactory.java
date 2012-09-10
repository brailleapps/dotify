package org.daisy.dotify.setups;

import java.util.Set;

import org.daisy.dotify.setups.common.CommonResourceLocator;
import org.daisy.dotify.system.InputManager;
import org.daisy.dotify.system.InputManagerFactory;
import org.daisy.dotify.text.FilterLocale;

public class XMLInputManagerFactory implements InputManagerFactory {
	private final LocalizationResourceLocator locator;
	
	public XMLInputManagerFactory() {
		this.locator = new LocalizationResourceLocator();
	}

	public boolean supportsLocale(FilterLocale locale) {
		return locator.supportsLocale(locale);
	}
	
	public Set<String> listSupportedLocales() {
		return locator.listSupportedLocales();
	}

	public InputManager newInputManager(FilterLocale locale) {
        return new XMLInputManager(locator.getResourceLocator(locale), new CommonResourceLocator());
	}

}
