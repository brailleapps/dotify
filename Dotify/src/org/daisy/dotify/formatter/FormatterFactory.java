package org.daisy.dotify.formatter;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;

/**
 * Provides a factory for formatters. The factory will instantiate 
 * the first Formatter it encounters when querying the services API. 
 * 
 * @author Joel Håkansson
 */
public class FormatterFactory {
	private BrailleTranslator factory;
	private FilterLocale locale;
	
	protected FormatterFactory() {
		factory = null;
		locale = null;
	}

	public static FormatterFactory newInstance() {
		Iterator<FormatterFactory> i = ServiceRegistry.lookupProviders(FormatterFactory.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new FormatterFactory();
	}
	
	public void setTranslator(BrailleTranslator factory) {
		this.factory = factory;
	}
	/*
	public void setLocale(FilterLocale locale) {
		this.locale = locale;
	}*/
	
	public Formatter newFormatter() {
		Iterator<Formatter> i = ServiceRegistry.lookupProviders(Formatter.class);
		while (i.hasNext()) {
			Formatter f = i.next();
			if (factory!=null) { 
				f.setBrailleTranslator(factory);
			}
			/*
			if (locale!=null) {
				f.setLocale(locale);
			}*/
			return f;
		}
		throw new RuntimeException("Cannot find formatter.");
	}
}
