package org.daisy.dotify.formatter;

import java.util.Iterator;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.translator.BrailleTranslator;

/**
 * Provides a factory for formatters. The factory will instantiate 
 * the first Formatter it encounters when querying the services API. 
 * 
 * @author Joel Håkansson
 */
public class FormatterFactory {
	private BrailleTranslator factory;
	private final FormatterProxy proxy;
	
	protected FormatterFactory() {
		factory = null;
		//Gets the first formatter (assumes there is at least one).
		proxy = ServiceRegistry.lookupProviders(FormatterProxy.class).next();
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
	
	public Formatter newFormatter() {
		Formatter ret = proxy.newFormatter();
		if (factory!=null) { 
			ret.setBrailleTranslator(factory);
		}
		return ret;
	}
}
