package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.formatter.Volume;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.tools.StateObject;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson
 */
public class FormatterImpl implements Formatter {

	private final BlockStructImpl flowStruct;
	private final StateObject state;
	private final BrailleTranslator translator;

	private final Stack<VolumeTemplateImpl> volumeTemplates;
	private HashMap<String, TableOfContentsImpl> tocs;
	private final ExpressionFactory ef;
	private FormatterCoreImpl core;

	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslator translator, ExpressionFactory ef) {
		this.flowStruct = new BlockStructImpl(); //masters
		this.state = new StateObject();
		this.translator = translator;

		this.volumeTemplates = new Stack<VolumeTemplateImpl>();
		this.tocs = new HashMap<String, TableOfContentsImpl>();
		this.ef = ef;
	}

	public void open() {
		state.assertUnopened();
		state.open();
	}

	public void addLayoutMaster(String name, LayoutMaster master) {
		flowStruct.addLayoutMaster(name, master);
	}

	public void newSequence(SequenceProperties p) {
		state.assertOpen();
		flowStruct.newSequence(p);
		core = new FormatterCoreImpl(flowStruct.getCurrentSequence(), translator);
	}

	/**
	 * Gets the resulting data structure
	 * @return returns the data structure
	 * @throws IllegalStateException if not closed 
	 */
	public BlockStruct getFlowStruct() {
		state.assertClosed();
		return flowStruct;
	}
	
	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}

	public BrailleTranslator getTranslator() {
		return translator;
	}

	private VolumeContentFormatter getVolumeContentFormatter() {
		return new BlockEventHandlerRunner(flowStruct.getMasters(), tocs, volumeTemplates, getTranslator(), ef);
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
		paginator.open(getTranslator(), getFlowStruct().getBlockSequenceIterable());

		BookStruct bookStruct = new BookStruct(paginator, getVolumeContentFormatter(), getTranslator());
		return bookStruct.getVolumes();
	}

	public void startBlock(BlockProperties props) {
		state.assertOpen();
		core.startBlock(props);
	}

	public void startBlock(BlockProperties props, String blockId) {
		state.assertOpen();
		core.startBlock(props, blockId);
	}

	public void endBlock() {
		state.assertOpen();
		core.endBlock();
	}

	public void startFloat(String id) {
		state.assertOpen();
		core.startFloat(id);
	}

	public void endFloat() {
		state.assertOpen();
		core.endFloat();
	}

	public void insertMarker(Marker marker) {
		state.assertOpen();
		core.insertMarker(marker);
	}

	public void insertAnchor(String ref) {
		state.assertOpen();
		core.insertAnchor(ref);
	}

	public void insertLeader(Leader leader) {
		state.assertOpen();
		core.insertLeader(leader);
	}

	public void addChars(CharSequence chars, TextProperties props) {
		state.assertOpen();
		core.addChars(chars, props);
	}

	public void newLine() {
		state.assertOpen();
		core.newLine();
	}

	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		state.assertOpen();
		core.insertReference(identifier, numeralStyle);
	}

	public void insertEvaluate(String exp, TextProperties t) {
		state.assertOpen();
		core.insertEvaluate(exp, t);
	}

}
