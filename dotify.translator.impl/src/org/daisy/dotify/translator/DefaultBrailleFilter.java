package org.daisy.dotify.translator;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.api.hyphenator.HyphenatorFactoryMakerService;
import org.daisy.dotify.api.hyphenator.HyphenatorInterface;
import org.daisy.dotify.api.translator.BrailleFilter;
import org.daisy.dotify.api.translator.MarkerProcessor;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.common.text.StringFilter;

public class DefaultBrailleFilter implements BrailleFilter {
	private final String loc;
	private final StringFilter filter;
	private final MarkerProcessor tap;
	private final HyphenatorFactoryMakerService hyphenatorFactoryMaker;
	private final Map<String, HyphenatorInterface> hyphenators;
	
	public DefaultBrailleFilter(StringFilter filter, String locale, HyphenatorFactoryMakerService hyphenatorFactoryMaker) {
		this(filter, locale, null, hyphenatorFactoryMaker);
	}
	
	public DefaultBrailleFilter(StringFilter filter, String locale, MarkerProcessor tap, HyphenatorFactoryMakerService hyphenatorFactoryMaker) {
		this.loc = locale;
		this.filter = filter;
		this.tap = tap;
		this.hyphenators = new HashMap<String, HyphenatorInterface>();
		this.hyphenatorFactoryMaker = hyphenatorFactoryMaker;
	}

	@Override
	public String filter(Translatable specification) throws TranslationException {
		String locale = specification.getLocale();
		if (locale==null) {
			locale = loc;
		}
		HyphenatorInterface h = hyphenators.get(locale);
		if (h == null && specification.isHyphenating()) {
			// if we're not hyphenating the language in question, we do not
			// need to
			// add it, nor throw an exception if it cannot be found.
			try {
				h = hyphenatorFactoryMaker.newHyphenator(locale);
			} catch (HyphenatorConfigurationException e) {
				throw new DefaultBrailleFilterException(e);
			}
			hyphenators.put(locale, h);
		}
		String text = specification.getText();
		if (tap != null) {
			text = tap.processAttributes(specification.getAttributes(), text);
		}
		//translate braille using the same filter, regardless of language
		return filter.filter(specification.isHyphenating()?h.hyphenate(text):text);
	}
	
	private class DefaultBrailleFilterException extends TranslationException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6102686243949860112L;

		DefaultBrailleFilterException(Throwable cause) {
			super(cause);
		}
		
	}

}
