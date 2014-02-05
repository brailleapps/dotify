package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;

public class FormatterContextImpl implements FormatterContext {

	private final BrailleTranslator translator;
	private final Map<String, LayoutMaster> masters;
	private HashMap<String, TableOfContentsImpl> tocs;
	private final Stack<VolumeTemplateImpl> volumeTemplates;
	private final char spaceChar;

	public FormatterContextImpl(BrailleTranslator translator) {
		this.translator = translator;
		this.masters = new HashMap<String, LayoutMaster>();
		this.tocs = new HashMap<String, TableOfContentsImpl>();
		this.volumeTemplates = new Stack<VolumeTemplateImpl>();
		//margin char can only be a single character, the reason for going through the translator
		//is because output isn't always braille.
		this.spaceChar = getTranslator().translate(" ").getTranslatedRemainder().charAt(0);
	}

	public BrailleTranslator getTranslator() {
		return translator;
	}

	public void addLayoutMaster(String name, LayoutMaster master) {
		masters.put(name, master);
	}
	
	public Map<String, LayoutMaster> getMasters() {
		return masters;
	}
	
	public TableOfContents newToc(String tocName) {
		TableOfContentsImpl toc = new TableOfContentsImpl();
		tocs.put(tocName, toc);
		return toc;
	}
	
	public Map<String, TableOfContentsImpl> getTocs() {
		return tocs;
	}
	
	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props) {
		VolumeTemplateImpl template = new VolumeTemplateImpl(props.getCondition(), props.getSplitterMax());
		volumeTemplates.push(template);
		return template;
	}
	
	public List<VolumeTemplateImpl> getVolumeTemplates() {
		return volumeTemplates;
	}
	
	public char getSpaceCharacter() {
		return spaceChar;
	}

}
