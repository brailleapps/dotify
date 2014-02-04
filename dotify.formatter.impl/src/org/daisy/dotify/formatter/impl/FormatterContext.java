package org.daisy.dotify.formatter.impl;

import java.util.Map;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.translator.BrailleTranslator;

/**
 * Provides formatter context data.
 * @author Joel Håkansson
 *
 */
interface FormatterContext {

	public BrailleTranslator getTranslator();
	
	public Map<String, LayoutMaster> getMasters();
	
	public void addLayoutMaster(String name, LayoutMaster master);
	
	public char getSpaceCharacter();
	
}
