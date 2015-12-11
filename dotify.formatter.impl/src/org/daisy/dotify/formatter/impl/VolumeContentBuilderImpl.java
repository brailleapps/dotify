package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.DynamicSequenceBuilder;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.Leader;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.NumeralStyle;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TextProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.api.formatter.VolumeContentBuilder;

class VolumeContentBuilderImpl extends Stack<VolumeSequence> implements VolumeContentBuilder {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3736631267650875060L;
	private final Map<String, TableOfContentsImpl> tocs;
	private final List<FormatterCore> formatters;
	private TocSequenceEventImpl tocSequence;

	public VolumeContentBuilderImpl(Map<String, TableOfContentsImpl> tocs) {
		this.tocs = tocs;
		this.formatters = new ArrayList<>();
		this.tocSequence = null;
	}

	@Override
	public void newSequence(SequenceProperties props) {
		StaticSequenceEventImpl volSeq = new StaticSequenceEventImpl(props);
		formatters.add(volSeq);
		tocSequence = null;
		add(volSeq);
	}

	@Override
	public void newTocSequence(TocProperties props) {
		tocSequence = new TocSequenceEventImpl(props, tocs.get(props.getTocName()), props.getRange(), null);
		add(tocSequence);
	}

	@Override
	public void newOnTocStart(Condition useWhen) {
		formatters.add(tocSequence.addTocStart(useWhen));
	}

	@Override
	public void newOnTocStart() {
		formatters.add(tocSequence.addTocStart(null));
	}

	@Override
	public void newOnVolumeStart(Condition useWhen) {
		formatters.add(tocSequence.addVolumeStartEvents(useWhen));
	}

	@Override
	public void newOnVolumeStart() {
		formatters.add(tocSequence.addVolumeStartEvents(null));
	}

	@Override
	public void newOnVolumeEnd(Condition useWhen) {
		formatters.add(tocSequence.addVolumeEndEvents(useWhen));
	}

	@Override
	public void newOnVolumeEnd() {
		formatters.add(tocSequence.addVolumeEndEvents(null));
	}

	@Override
	public void newOnTocEnd(Condition useWhen) {
		formatters.add(tocSequence.addTocEnd(useWhen));
	}

	@Override
	public void newOnTocEnd() {
		formatters.add(tocSequence.addTocEnd(null));
	}
	
	private FormatterCore current() {
		return formatters.get(formatters.size()-1);
	}

	@Override
	public void startBlock(BlockProperties props) {
		current().startBlock(props);
	}

	@Override
	public void startBlock(BlockProperties props, String blockId) {
		current().startBlock(props, blockId);
	}

	@Override
	public void endBlock() {
		current().endBlock();
	}

	@Override
	public void insertMarker(Marker marker) {
		current().insertMarker(marker);
	}

	@Override
	public void insertAnchor(String ref) {
		current().insertAnchor(ref);
	}

	@Override
	public void insertLeader(Leader leader) {
		current().insertLeader(leader);
	}

	@Override
	public void addChars(CharSequence chars, TextProperties props) {
		current().addChars(chars, props);
	}

	@Override
	public void newLine() {
		current().newLine();
	}

	@Override
	public void insertReference(String identifier, NumeralStyle numeralStyle) {
		current().insertReference(identifier, numeralStyle);
	}

	@Override
	public void insertEvaluate(DynamicContent exp, TextProperties t) {
		current().insertEvaluate(exp, t);
	}

	@Override
	public DynamicSequenceBuilder newDynamicSequence(SequenceProperties props) {
		DynamicSequenceEventImpl dsb = new DynamicSequenceEventImpl(props);
		add(dsb);
		return dsb;
	}

}
