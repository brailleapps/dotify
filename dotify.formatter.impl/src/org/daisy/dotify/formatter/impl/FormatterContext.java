package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;

/**
 * Provides formatter context data.
 * @author Joel Håkansson
 *
 */
class FormatterContext {

	private final BrailleTranslator translator;
	private final Map<String, LayoutMaster> masters;
	private final Map<String, ContentCollectionImpl> collections;
	private final char spaceChar;

	FormatterContext(BrailleTranslator translator) {
		this.translator = translator;
		this.masters = new HashMap<String, LayoutMaster>();
		this.collections = new HashMap<String, ContentCollectionImpl>();
		//margin char can only be a single character, the reason for going through the translator
		//is because output isn't always braille.
		this.spaceChar = getTranslator().translate(" ").getTranslatedRemainder().charAt(0);
	}

	BrailleTranslator getTranslator() {
		return translator;
	}
	
	LayoutMasterBuilder newLayoutMaster(String name, LayoutMasterProperties properties) {
		LayoutMaster master = new LayoutMaster(properties);
		masters.put(name, master);
		return master;
	}
	
	ContentCollectionImpl newContentCollection(String collectionId) {
		ContentCollectionImpl collection = new ContentCollectionImpl();
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
