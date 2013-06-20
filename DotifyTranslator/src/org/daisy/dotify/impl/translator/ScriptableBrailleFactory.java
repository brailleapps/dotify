package org.daisy.dotify.impl.translator;

import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.script.ScriptException;

import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.translator.BrailleTranslator;
import org.daisy.dotify.translator.BrailleTranslatorFactory;
import org.daisy.dotify.translator.UnsupportedSpecificationException;

/**
 * Provides a braille factory for javascript implementations.
 * The factory is populated using an XML properties file called
 * script-catalog.xml which can be located in either <tt>resource-files/translators</tt>
 * or <tt>scripts/translators</tt>. The file is loaded using 
 * <tt>getResource()</tt> or, if that fails, using <tt>new File()</tt>.
 * This allows for both stand-alone distribution (jar) and easy overriding
 * using the directory structure of the application. In either case,
 * all paths must be relative to either this package's resource-files folder
 * or the application's scripts folder. 
 * 
 * @author Joel Håkansson
 */
public class ScriptableBrailleFactory implements BrailleTranslatorFactory {
	private final Properties catalog;
	private final Logger logger;
	private final Map<String, Properties> impl; 

	public ScriptableBrailleFactory() {
		logger = Logger.getLogger(this.getClass().getCanonicalName());
		catalog = loadProperties("translators/script-catalog.xml");
		impl = new Hashtable<String, Properties>();
	}
	
	private Properties loadProperties(String path) {
		Properties p = new Properties();
		try {
			InputStream is = StreamFetcher.getInputStream(path);
			try {
				p.loadFromXML(is);
			} finally {
				is.close();
			}
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to load file: " + path, e);
		}
		return p;
	}
	
	private String getImplPath(FilterLocale locale, String mode) {
		String path = catalog.getProperty(locale.toString());
		if (path!=null) {
			if (impl.get(path)==null) {
				impl.put(path, loadProperties(path));
			}
			return impl.get(path).getProperty(mode);
		} else {
			return null;
		}
	}

	@Override
	public boolean supportsSpecification(FilterLocale locale, String mode) {
		return getImplPath(locale, mode)!=null;
	}

	@Override
	public BrailleTranslator newTranslator(FilterLocale locale, String mode)
			throws UnsupportedSpecificationException {
		String implPath = getImplPath(locale, mode);
		if (implPath!=null) {
			try {
				return new ScriptableBrailleTranslator(implPath);
			} catch (IllegalArgumentException e) {
				throw new UnsupportedSpecificationException(e);
			} catch (ScriptException e) {
				throw new UnsupportedSpecificationException(e);
			}
		} else {
			throw new UnsupportedSpecificationException("Factory does not support " + locale + "/" + mode);
		}
	}

}
