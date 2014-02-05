package org.daisy.dotify.formatter.impl;

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

	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslator translator) {
		this(new FormatterContextImpl(translator));
	}
	
	public FormatterImpl(FormatterContext context) {
		super(context);
	}

	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props) {
		return context.newVolumeTemplate(props);
	}

	public TableOfContents newToc(String tocName) {
		return context.newToc(tocName);
	}

	public Iterable<Volume> getVolumes() {
		PaginatorImpl paginator = new PaginatorImpl(context, getFlowStruct().getBlockSequenceIterable());
		BookStruct bookStruct = new BookStruct(context, paginator);
		return bookStruct.getVolumes();
	}

}
