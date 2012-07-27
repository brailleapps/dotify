package org.daisy.dotify.hyphenator.latex;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;

public class LatexHyphenator implements HyphenatorInterface {
	private final Logger logger;
	private Properties tables;
	private final Map<FilterLocale, net.davidashen.text.Hyphenator> cache;
	
	public LatexHyphenator() {
		logger = Logger.getLogger(LatexHyphenator.class.getCanonicalName());
		tables = new Properties();
		cache = new HashMap<FilterLocale, net.davidashen.text.Hyphenator>();
		try {
	        URL tablesURL = this.getClass().getResource("hyphenation_tables.xml");
	        if(tablesURL!=null){
	        	tables.loadFromXML(tablesURL.openStream());
	        } else {
	        	throw new IOException("Cannot locate hyphenation tables");
	        }
		} catch (IOException e) {
			logger.warning("Failed to load table list.");
		}
	}

	public boolean supportsLocale(FilterLocale locale) {
		return tables.getProperty(locale.toString())!=null;
	}

	private net.davidashen.text.Hyphenator loadHyphenator(String languageFileRelativePath) {
		net.davidashen.text.Hyphenator hyphenator = new net.davidashen.text.Hyphenator();
        InputStream language = this.getClass().getResourceAsStream(languageFileRelativePath);
        hyphenator.setErrorHandler(new HyphenatorErrorHandler(languageFileRelativePath));
        try {
			hyphenator.loadTable(language);
		} catch (IOException e) {
			new RuntimeException("Failed to load resource: " + languageFileRelativePath);
		}
        return hyphenator;
	}
	
	private net.davidashen.text.Hyphenator getHyphenator(FilterLocale locale) throws UnsupportedLocaleException {
		net.davidashen.text.Hyphenator h = cache.get(locale);
		if (h!=null) { 
			return h;
		} else {
	        String languageFileRelativePath = tables.getProperty(locale.toString());
	        if(languageFileRelativePath==null) {
	        	throw new UnsupportedLocaleException("Locale not supported: " + locale.toString());
	        } else {
	    		logger.fine("Loading hyphenation file: " + languageFileRelativePath);
	    		h = loadHyphenator(languageFileRelativePath);
	    		cache.put(locale, h);
	    		return h;
	        }
		}
	}

	public String hyphenate(String phrase, FilterLocale locale, int beginLimit,
			int endLimit) throws UnsupportedLocaleException {
		return getHyphenator(locale).hyphenate(phrase, beginLimit, endLimit);
	}

}
