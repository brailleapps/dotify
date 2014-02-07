package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.Volume;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson
 */
public class FormatterImpl extends BlockStruct implements Formatter {
	private HashMap<String, TableOfContentsImpl> tocs;
	private final Stack<VolumeTemplate> volumeTemplates;
	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslator translator) {
		this(new FormatterContextImpl(translator));
	}
	
	public FormatterImpl(FormatterContext context) {
		super(context);
		this.tocs = new HashMap<String, TableOfContentsImpl>();
		this.volumeTemplates = new Stack<VolumeTemplate>();
	}

	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props) {
		VolumeTemplate template = new VolumeTemplate(tocs, props.getCondition(), props.getSplitterMax());
		volumeTemplates.push(template);
		return template;
	}
	
	public List<VolumeTemplate> getVolumeTemplates() {
		return volumeTemplates;
	}

	public TableOfContents newToc(String tocName) {
		TableOfContentsImpl toc = new TableOfContentsImpl();
		tocs.put(tocName, toc);
		return toc;
	}
	
	public Map<String, TableOfContentsImpl> getTocs() {
		return tocs;
	}

	public Iterable<Volume> getVolumes() {
		PaginatorImpl paginator = new PaginatorImpl(context, getFlowStruct().getBlockSequenceIterable());
		BookStruct bookStruct = new BookStruct(volumeTemplates, context, paginator);
		return bookStruct.getVolumes();
	}

}
