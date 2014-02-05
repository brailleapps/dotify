package org.daisy.dotify.formatter.impl;

import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;

/**
 * Provides formatter context data.
 * @author Joel Håkansson
 *
 */
interface FormatterContext {

	public BrailleTranslator getTranslator();
	
	public Map<String, LayoutMaster> getMasters();
	
	public Map<String, TableOfContentsImpl> getTocs();
	
	public List<VolumeTemplateImpl> getVolumeTemplates();
	
	public void addLayoutMaster(String name, LayoutMaster master);
	
	public TableOfContents newToc(String tocName);
	
	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props);
	
	public char getSpaceCharacter();
	
}
