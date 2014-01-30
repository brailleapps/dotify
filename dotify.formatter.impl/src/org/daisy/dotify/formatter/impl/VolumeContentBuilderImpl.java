package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.api.formatter.VolumeContentBuilder;
import org.daisy.dotify.api.obfl.ExpressionFactory;

class VolumeContentBuilderImpl extends Stack<VolumeSequenceEvent> implements VolumeContentBuilder {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3736631267650875060L;
	private final ExpressionFactory ef;
	private final VolumeTemplateImpl template;
	private List<FormatterCore> formatters;
	private TocSequenceEventImpl tocSequence;

	public VolumeContentBuilderImpl(ExpressionFactory ef, VolumeTemplateImpl template) {
		this.ef = ef;
		this.template = template;
		this.formatters = new ArrayList<FormatterCore>();
		this.tocSequence = null;
	}

	public void newSequence(SequenceProperties props) {
		StaticSequenceEventImpl volSeq = new StaticSequenceEventImpl(props);
		formatters.add(volSeq);
		tocSequence = null;
		add(volSeq);
	}

	public void newTocSequence(TocProperties props) {
		tocSequence = new TocSequenceEventImpl(props, props.getTocName(), props.getRange(), props.getUseWhen(), null, template, ef);
		add(tocSequence);
	}

	public void newOnTocStart(String useWhen) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addTocStartEvents(f, useWhen);
		formatters.add(f);
	}

	public void newOnTocStart() {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addTocStartEvents(f, null);
		formatters.add(f);
	}

	public void newOnVolumeStart(String useWhen) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addVolumeStartEvents(f, useWhen);
		formatters.add(f);
	}

	public void newOnVolumeStart() {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addVolumeStartEvents(f, null);
		formatters.add(f);
	}

	public void newOnVolumeEnd(String useWhen) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addVolumeEndEvents(f, useWhen);
		formatters.add(f);
	}

	public void newOnVolumeEnd() {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addVolumeEndEvents(f, null);
		formatters.add(f);
	}

	public void newOnTocEnd(String useWhen) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addTocEndEvents(f, useWhen);
		formatters.add(f);
	}

	public void newOnTocEnd() {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocSequence.addTocEndEvents(f, null);
		formatters.add(f);
	}
	
	private FormatterCore current() {
		return formatters.get(formatters.size()-1);
	}

	public void startBlock(BlockProperties props) {
		current().startBlock(props);
	}

	public void startBlock(BlockProperties props, String blockId) {
		current().startBlock(props, blockId);
	}

	public void endBlock() {
		current().endBlock();
	}

	public void startFloat(String id) {
		current().startFloat(id);
	}

	public void endFloat() {
		current().endFloat();
	}

	public void insertMarker(Marker marker) {
		current().insertMarker(marker);
	}

	public void insertAnchor(String ref) {
		current().insertAnchor(ref);
	}

	public void insertLeader(Leader leader) {
		current().insertLeader(leader);
	}

	public void addChars(CharSequence chars, TextProperties props) {
		current().addChars(chars, props);
	}

	public void newLine() {
		current().newLine();
	}

	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		current().insertReference(identifier, numeralStyle);
	}

	public void insertEvaluate(String exp, TextProperties t) {
		current().insertEvaluate(exp, t);
	}

}
