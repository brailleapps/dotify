package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
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
public class FormatterImpl extends BlockStructImpl implements Formatter {

	private final Stack<VolumeTemplateImpl> volumeTemplates;
	private HashMap<String, TableOfContentsImpl> tocs;

	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslator translator) {
		this(new FormatterContextImpl(translator));
	}
	
	public FormatterImpl(FormatterContext context) {
		super(context);

		this.volumeTemplates = new Stack<VolumeTemplateImpl>();
		this.tocs = new HashMap<String, TableOfContentsImpl>();
	}

	public BrailleTranslator getTranslator() {
		return context.getTranslator();
	}
	
	private VolumeContentFormatter getVolumeContentFormatter() {
		return new BlockEventHandlerRunner(tocs, volumeTemplates, context);
	}


	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props) {
		VolumeTemplateImpl template = new VolumeTemplateImpl(props.getCondition(), props.getSplitterMax());
		volumeTemplates.push(template);
		return template;
	}

	public TableOfContents newToc(String tocName) {
		TableOfContentsImpl toc = new TableOfContentsImpl();
		tocs.put(tocName, toc);
		return toc;
	}

	public Iterable<Volume> getVolumes() {
		PaginatorImpl paginator = new PaginatorImpl(context, getFlowStruct().getBlockSequenceIterable());
		BookStruct bookStruct = new BookStruct(paginator, getVolumeContentFormatter(), context.getTranslator());
		return bookStruct.getVolumes();
	}



}
