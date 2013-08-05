package org.daisy.dotify.formatter;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a factory for formatters. The factory will instantiate 
 * the first Formatter it encounters when querying the services API. 
 * 
 * @author Joel Håkansson
 */
public class FormatterFactoryMaker {
	private final FormatterFactory proxy;
	
	protected FormatterFactoryMaker() {
		//Gets the first formatter (assumes there is at least one).
		proxy = ServiceRegistry.lookupProviders(FormatterFactory.class).next();
	}

	public static FormatterFactoryMaker newInstance() {
		Iterator<FormatterFactoryMaker> i = ServiceRegistry.lookupProviders(FormatterFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new FormatterFactoryMaker();
	}
	
	public FormatterFactory getFactory(FilterLocale locale, String mode) {
		return proxy;
	}
	
	public Formatter newFormatter(FilterLocale locale, String mode) {
		return getFactory(locale, mode).newFormatter(locale, mode);
	}
}
