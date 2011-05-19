package org.daisy.dotify.input;

import org.daisy.dotify.system.AbstractResourceLocator;
import org.daisy.dotify.system.ResourceLocator;
import org.daisy.dotify.system.tasks.layout.text.FilterLocale;

public class InputManagerTaskSystemFactory {
	private final FilterLocale sv;
	private final FilterLocale en;
	private final ResourceLocator locator;
	
	private InputManagerTaskSystemFactory() {
		sv = FilterLocale.parse("sv");
		en = FilterLocale.parse("en");
		locator = new InputDetectorTaskSystemResourceLocator();
	}
	
	public static InputManagerTaskSystemFactory newInstance() {
		return new InputManagerTaskSystemFactory();
	}
	
	public InputManagerTaskSystem newInputDetectorTaskSystem(FilterLocale locale) {
		if (locale.isA(sv)) {
			return new InputManagerTaskSystem(locator, "sv_SE/config/", "common/config/");
		} else if (locale.isA(en)) {
			return new InputManagerTaskSystem(locator, "en_US/config/", "common/config/");
		}
		throw new IllegalArgumentException("Cannot locate an InputDetectorTaskSystem for " + locale);
	}
	
	private class InputDetectorTaskSystemResourceLocator extends AbstractResourceLocator {
	}

}
