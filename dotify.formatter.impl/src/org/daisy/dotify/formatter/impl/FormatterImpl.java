package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Stack;

import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.Volume;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.BrailleTranslator;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson
 */
public class FormatterImpl extends BlockStructImpl implements Formatter {

	private final Stack<VolumeTemplateImpl> volumeTemplates;
	private HashMap<String, TableOfContentsImpl> tocs;
	private final ExpressionFactory ef;

	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslator translator, ExpressionFactory ef) {
		this(new FormatterContextImpl(translator), ef);
	}
	
	public FormatterImpl(FormatterContext context, ExpressionFactory ef) {
		super(context);

		this.volumeTemplates = new Stack<VolumeTemplateImpl>();
		this.tocs = new HashMap<String, TableOfContentsImpl>();
		this.ef = ef;
	}

	public BrailleTranslator getTranslator() {
		return context.getTranslator();
	}
	
	private VolumeContentFormatter getVolumeContentFormatter() {
		return new BlockEventHandlerRunner(tocs, volumeTemplates, context, ef);
	}


	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props) {
		VolumeTemplateImpl template = new VolumeTemplateImpl(props.getUseWhen(), props.getSplitterMax(), ef);
		volumeTemplates.push(template);
		template.setVolumeCountVariableName(props.getVolumeCountVariable());
		template.setVolumeNumberVariableName(props.getVolumeNumberVariable());
		return template;
	}

	public TableOfContents newToc(String tocName) {
		TableOfContentsImpl toc = new TableOfContentsImpl();
		tocs.put(tocName, toc);
		return toc;
	}

	public Iterable<Volume> getVolumes() {
		PaginatorImpl paginator = new PaginatorImpl();
		paginator.open(context, getFlowStruct().getBlockSequenceIterable());

		BookStruct bookStruct = new BookStruct(paginator, getVolumeContentFormatter(), context.getTranslator());
		return bookStruct.getVolumes();
	}



}
