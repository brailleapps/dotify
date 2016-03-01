package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.api.translator.TranslatorConfigurationException;

/**
 * Provides formatter context data.
 * @author Joel Håkansson
 *
 */
class FormatterContext extends FormatterCoreContext {
	private final String locale;
	private final BrailleTranslator translator;
	private final BrailleTranslatorFactoryMakerService translatorFactory;
	private final Map<String, BrailleTranslator> cache;
	private final Map<String, LayoutMaster> masters;
	private final Map<String, ContentCollectionImpl> collections;
	private final char spaceChar;

	FormatterContext(BrailleTranslatorFactoryMakerService translatorFactory, TextBorderFactoryMakerService tbf, String locale, String mode) {
		super(tbf, mode);
		this.translatorFactory = translatorFactory;
		this.cache = new HashMap<>();
		try {
			this.translator = translatorFactory.newTranslator(locale, mode);
			cache.put(mode, translator);
		} catch (TranslatorConfigurationException e) {
			throw new IllegalArgumentException(e);
		}
		this.masters = new HashMap<>();
		this.collections = new HashMap<>();
		//margin char can only be a single character, the reason for going through the translator
		//is because output isn't always braille.
		try {
			this.spaceChar = getDefaultTranslator().translate(Translatable.text(" ").build()).getTranslatedRemainder().charAt(0);
		} catch (TranslationException e) {
			throw new RuntimeException(e);
		}
		this.locale = locale;
	}

	BrailleTranslator getDefaultTranslator() {
		return translator;
	}
	
	BrailleTranslator getTranslator(String mode) {
		if (mode==null) {
			return translator;
		}
		BrailleTranslator ret = cache.get(mode);
		if (ret==null) {
			try {
				ret = translatorFactory.newTranslator(locale, mode);
			} catch (TranslatorConfigurationException e) {
				throw new IllegalArgumentException(e);
			}
			cache.put(mode, ret);
		}
		return ret;
	}
	
	LayoutMasterBuilder newLayoutMaster(String name, LayoutMasterProperties properties) {
		LayoutMaster master = new LayoutMaster(this, properties);
		masters.put(name, master);
		return master;
	}
	
	ContentCollectionImpl newContentCollection(String collectionId) {
		ContentCollectionImpl collection = new ContentCollectionImpl(this);
		collections.put(collectionId, collection);
		return collection;
	}
	
	Map<String, LayoutMaster> getMasters() {
		return masters;
	}
	
	Map<String, ContentCollectionImpl> getCollections() {
		return collections;
	}
	
	char getSpaceCharacter() {
		return spaceChar;
	}

}
