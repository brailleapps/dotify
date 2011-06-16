package org.daisy.dotify.formatter;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterFactory;
import org.daisy.dotify.text.FilterLocale;

public class FormatterFactory {
	private FilterFactory factory;
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
	
	public void setFilterFactory(FilterFactory factory) {
		this.factory = factory;
	}
	
	public void setLocale(FilterLocale locale) {
		this.locale = locale;
	}
	
	public Formatter newFormatter() {
		Iterator<Formatter> i = ServiceRegistry.lookupProviders(Formatter.class);
		while (i.hasNext()) {
			Formatter f = i.next();
			if (factory!=null) { 
				f.setFilterFactory(factory);
			}
			if (locale!=null) {
				f.setLocale(locale);
			}
			return f;
		}
		throw new RuntimeException("Cannot find formatter.");
	}
}
