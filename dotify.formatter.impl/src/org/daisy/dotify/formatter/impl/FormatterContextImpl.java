package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;

public class FormatterContextImpl implements FormatterContext {

	private final BrailleTranslator translator;
	private final Map<String, LayoutMaster> masters;
	private final char spaceChar;

	public FormatterContextImpl(BrailleTranslator translator) {
		this.translator = translator;
		this.masters = new HashMap<String, LayoutMaster>();
		//margin char can only be a single character, the reason for going through the translator
		//is because output isn't always braille.
		this.spaceChar = getTranslator().translate(" ").getTranslatedRemainder().charAt(0);
	}

	public BrailleTranslator getTranslator() {
		return translator;
	}
	
	public LayoutMasterBuilder newLayoutMaster(String name, LayoutMasterProperties properties) {
		LayoutMasterImpl master = new LayoutMasterImpl(properties);
		masters.put(name, master);
		return master;
	}
	
	public Map<String, LayoutMaster> getMasters() {
		return masters;
	}
	
	public char getSpaceCharacter() {
		return spaceChar;
	}

}
