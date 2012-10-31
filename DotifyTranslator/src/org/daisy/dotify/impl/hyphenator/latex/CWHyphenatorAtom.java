package org.daisy.dotify.impl.hyphenator.latex;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.hyphenator.UnsupportedLocaleException;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.SplitResult;
import org.daisy.dotify.text.StringSplitter;

public class CWHyphenatorAtom {
	public final static String DICTIONARY_KEY = "dictionary";
	public final static String EXCEPTIONS_FILE_KEY = "exceptions-file";
	public final static String DECOMPOUND_LIMIT_KEY = "decompound-limit";
	public final static String MINIMUM_WORD_LENGTH_KEY = "minimum-word-length";
	public final static String PATTERN_PROPERTIES_KEY = "pattern-properties";
	private final static int DEFAULT_DECOMPOUND_LIMIT = 6;
	private final static int DEFAULT_MINIMUM_WORD_LENGTH = 2;
	
	private final Logger logger;
	private final HyphenationConfig base;
	private final Properties exceptions;
	private final CWDecompounder decompounder;
	
	public CWHyphenatorAtom(String subPath, FilterLocale locale) throws UnsupportedLocaleException {
		logger = Logger.getLogger(this.getClass().getCanonicalName());

        Properties imp;
        if(subPath==null) {
        	throw new UnsupportedLocaleException("Locale not supported: " + locale.toString());
        } else {
    		logger.fine("Loading implementation: " + subPath);
    		imp = loadProperties(subPath);
        }
        
        Properties patternProps;
        if (imp.containsKey(PATTERN_PROPERTIES_KEY)) {
        	patternProps = loadProperties(imp.get(PATTERN_PROPERTIES_KEY).toString());
        } else {
        	throw new RuntimeException("Missing property: " + PATTERN_PROPERTIES_KEY);
        }
        
        base = new HyphenationConfig(patternProps);

        String exceptionsPath = imp.getProperty(EXCEPTIONS_FILE_KEY);
		if (exceptionsPath!=null && !exceptionsPath.equals("")) {
			exceptions = loadProperties(exceptionsPath);
		} else {
			exceptions = new Properties();
		}

		int decompLimit = DEFAULT_DECOMPOUND_LIMIT;
		try {
			decompLimit = Integer.parseInt(imp.getProperty(DECOMPOUND_LIMIT_KEY));
		} catch (Exception e) {
			logger.log(Level.FINE, "Could not parse decompound limit", e);
		}
		
		decompounder = new CWDecompounder(decompLimit);
		
		int minWord = DEFAULT_MINIMUM_WORD_LENGTH;
		try {
			minWord = Integer.parseInt(imp.getProperty(MINIMUM_WORD_LENGTH_KEY));
		} catch (Exception e) {
			logger.log(Level.FINE, "Could not parse minimum word length", e);
		}

		String url = imp.getProperty(DICTIONARY_KEY);
		try {
			decompounder.loadDictionary(url, minWord);
		} catch (IOException e) {
			logger.log(Level.WARNING, "Failed to read frequency file: " + url, e);
		}
	}
	
	private Properties loadProperties(String path) {
		Properties ret = new Properties();
		try {
	        URL propertiesURL = this.getClass().getResource(path);
	        if (propertiesURL!=null){
	        	ret.loadFromXML(propertiesURL.openStream());
	        } else {
	        	logger.warning("Cannot locate properties file: " + path);
	        }
		} catch (IOException e) {
			logger.warning("Failed to load properties file: " + path);
		}
		return ret;
	}
	
	public int getDefaultBeginLimit() {
		return base.getDefaultBeginLimit();
	}

	public int getDefaultEndLimit() {
		return base.getDefaultEndLimit();
	}

	public String hyphenate(String input, int beginLimit, int endLimit) {
		StringBuffer output = new StringBuffer();
		for (SplitResult sr : StringSplitter.split(input, "\\p{L}+")) {
			if (sr.isMatch()) {
				String word = sr.getText();
				if (exceptions.containsKey(word)) {
					output.append((String)exceptions.get(word));
				} else {
					output.append(decompounder.findCompounds(word, beginLimit, endLimit, false));
				}
			} else {
				output.append(sr.getText());
			}
		}
		return base.getHyphenator().hyphenate(output.toString(), beginLimit, endLimit);
	}
}
