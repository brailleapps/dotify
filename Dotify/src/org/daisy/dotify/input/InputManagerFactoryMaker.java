package org.daisy.dotify.input;

import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.text.FilterLocale;

/**
 * Provides a factory maker for input manager factories, that is to say a collection of 
 * @author joha
 *
 */
public abstract class InputManagerFactoryMaker {

	protected InputManagerFactoryMaker() { }

	public final static InputManagerFactoryMaker newInstance() {
		Iterator<InputManagerFactoryMaker> i = ServiceRegistry.lookupProviders(InputManagerFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new DefaultInputManagerFactoryMaker();
	}

	public abstract InputManagerFactory getFactory(FilterLocale locale, String fileFormat);
	
	public abstract Set<String> listSupportedLocales();

	public InputManager newInputManager(FilterLocale locale, String fileFormat) {
		Logger.getLogger(this.getClass().getCanonicalName()).fine("Attempt to locate an input manager for " + locale + "/" + fileFormat);
		return getFactory(locale, fileFormat).newInputManager(locale, fileFormat);
	}

}
