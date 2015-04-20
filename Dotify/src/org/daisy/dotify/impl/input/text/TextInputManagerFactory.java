package org.daisy.dotify.impl.input.text;

import java.util.HashSet;
import java.util.Set;

import org.daisy.dotify.input.InputManager;
import org.daisy.dotify.input.InputManagerFactory;
import org.daisy.dotify.text.FilterLocale;

public class TextInputManagerFactory implements InputManagerFactory {
	private final Set<String> locales;
	private final Set<String> formats;

	public TextInputManagerFactory() {
		this.locales = new HashSet<String>();
		this.locales.add("sv-SE");
		this.locales.add("en-US");
		this.formats = new HashSet<String>();
		this.formats.add("text");
		this.formats.add("txt");
	}

	public boolean supportsSpecification(FilterLocale locale, String fileFormat) {
		return formats.contains(fileFormat);
	}

	public InputManager newInputManager(FilterLocale locale, String fileFormat) {
		return new TextInputManager(locale.toString());
	}

	public Set<String> listSupportedLocales() {
		return new HashSet<String>(locales);
	}

	public Set<String> listSupportedFileFormats() {
		return new HashSet<String>(formats);
	}

}
