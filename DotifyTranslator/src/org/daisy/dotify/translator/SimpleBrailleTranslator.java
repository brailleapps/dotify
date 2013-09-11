package org.daisy.dotify.translator;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.hyphenator.HyphenatorConfigurationException;
import org.daisy.dotify.hyphenator.HyphenatorFactoryMaker;
import org.daisy.dotify.hyphenator.HyphenatorInterface;
import org.daisy.dotify.text.BreakPointHandler;
import org.daisy.dotify.text.FilterLocale;
import org.daisy.dotify.text.StringFilter;
import org.daisy.dotify.translator.attributes.MarkerProcessor;
import org.daisy.dotify.translator.attributes.TextAttribute;

/**
 * Provides a simple braille translator that translates
 * all texts using the same filter, regardless of language.
 * The translator does however switch hyphenation
 * rules based on language and it will throw an
 * exception if it cannot find the appropriate hyphenation
 * rules for the language.
 * @author Joel Håkansson
 *
 */
public class SimpleBrailleTranslator implements BrailleTranslator {
	private final FilterLocale locale;
	private final String translatorMode;
	private final StringFilter filter;
	private final MarkerProcessor tap;
	private final HyphenatorFactoryMaker hyphenatorFactoryMaker;
	private final Map<FilterLocale, HyphenatorInterface> hyphenators;
	
	private boolean hyphenating;
	
	public SimpleBrailleTranslator(StringFilter filter, FilterLocale locale, String translatorMode, MarkerProcessor tap) {
		this.filter = filter;
		this.locale = locale;
		this.translatorMode = translatorMode;
		this.tap = tap;
		this.hyphenating = true;
		this.hyphenators = new HashMap<FilterLocale, HyphenatorInterface>();
		this.hyphenatorFactoryMaker = HyphenatorFactoryMaker.newInstance();
	}

	public SimpleBrailleTranslator(StringFilter filter, FilterLocale locale, String translatorMode) {
		this(filter, locale, translatorMode, null);
	}

	public BrailleTranslatorResult translate(String text, FilterLocale locale, TextAttribute atts) throws TranslationException {
		HyphenatorInterface h = hyphenators.get(locale);
		if (h == null && isHyphenating()) {
			// if we're not hyphenating the language in question, we do not
			// need to
			// add it, nor throw an exception if it cannot be found.
			try {
				h = hyphenatorFactoryMaker.newHyphenator(locale);
			} catch (HyphenatorConfigurationException e) {
				throw new TranslationException(e);
			}
			hyphenators.put(locale, h);
		}
		if (tap != null) {
			text = tap.processAttributes(atts, text);
		}
		//translate braille using the same filter, regardless of language
		BreakPointHandler bph = new BreakPointHandler(filter.filter(isHyphenating()?h.hyphenate(text):text));
		return new DefaultBrailleTranslatorResult(bph, filter);
	}

	public BrailleTranslatorResult translate(String text, TextAttribute atts) {
		try {
			return translate(text, this.locale, atts);
		} catch (TranslationException e) {
			throw new RuntimeException("Coding error. This translator does not support the language it claims to support.");
		}
	}

	public BrailleTranslatorResult translate(String text, FilterLocale locale) throws TranslationException {
		return translate(text, locale, null);
	}

	public BrailleTranslatorResult translate(String text) {
		try {
			return translate(text, this.locale);
		} catch (TranslationException e) {
			throw new RuntimeException("Coding error. This translator does not support the language it claims to support.");
		}
	}

	public void setHyphenating(boolean value) {
		this.hyphenating = value;
	}

	public boolean isHyphenating() {
		return hyphenating;
	}

	public String getTranslatorMode() {
		return translatorMode;
	}

}
