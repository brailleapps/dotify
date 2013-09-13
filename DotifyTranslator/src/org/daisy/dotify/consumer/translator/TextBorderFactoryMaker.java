package org.daisy.dotify.consumer.translator;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Logger;

import javax.imageio.spi.ServiceRegistry;

import org.daisy.dotify.api.translator.TextBorderConfigurationException;
import org.daisy.dotify.api.translator.TextBorderFactory;
import org.daisy.dotify.api.translator.TextBorderStyle;

/**
 * Provides a text border factory maker. This class will look for
 * implementations of the TextBorderFactory interface using the
 * services API. It will return the first implementation that matches the
 * requested specification.
 * 
 * <p>
 * This class can be overridden by extending it and adding a reference to the
 * new implementation to the services API. This class will then choose the new
 * implementation when a new instance is requested.
 * </p>
 * 
 * @author Joel Håkansson
 * 
 */
public class TextBorderFactoryMaker implements TextBorderFactory {
	private final Logger logger;
	private final Map<String, Object> features;
	private TextBorderFactory last;

	private TextBorderFactoryMaker() {
		logger = Logger.getLogger(TextBorderFactoryMaker.class.getCanonicalName());
		this.features = new HashMap<String, Object>();
		this.last = null;
	}

	/**
	 * Creates a new instance of braille translator factory maker.
	 * @return returns a new braille translator factory maker.
	 */
	public static TextBorderFactoryMaker newInstance() {
		Iterator<TextBorderFactoryMaker> i = ServiceRegistry.lookupProviders(TextBorderFactoryMaker.class);
		while (i.hasNext()) {
			return i.next();
		}
		return new TextBorderFactoryMaker();
	}
	
	public TextBorderStyle newTextBorderStyle() throws TextBorderConfigurationException {
		if (last != null) {
			return last.newTextBorderStyle();
		} else {
			TextBorderStyle ret;
			Iterator<TextBorderFactory> i = ServiceRegistry.lookupProviders(TextBorderFactory.class);
			while (i.hasNext()) {
				TextBorderFactory h = i.next();
				for (String key : features.keySet()) {
					h.setFeature(key, features.get(key));
				}
				try {
					ret = h.newTextBorderStyle();
					last = h;
					return ret;
				} catch (TextBorderConfigurationException e) {
					// try another one
				}
			}
			last = null;
			throw new TextBorderFactoryMakerException();
		}
	}

	public void setFeature(String key, Object value) {
		last = null;
		features.put(key, value);
	}

	public Object getFeature(String key) {
		return features.get(key);
	}
	
	private class TextBorderFactoryMakerException extends TextBorderConfigurationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 7241556330716217110L;
		
	}

}
