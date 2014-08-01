package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.DynamicContent;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.ItemSequenceProperties;
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
	private List<FormatterCore> formatters;
	private TocSequenceEventImpl tocSequence;
	private ItemSequenceEventImpl itemSequence;

	public VolumeContentBuilderImpl(Map<String, TableOfContentsImpl> tocs) {
		this.tocs = tocs;
		this.formatters = new ArrayList<FormatterCore>();
		this.tocSequence = null;
		this.itemSequence = null;
	}

	public void newSequence(SequenceProperties props) {
		StaticSequenceEventImpl volSeq = new StaticSequenceEventImpl(props);
		formatters.add(volSeq);
		tocSequence = null;
		add(volSeq);
	}

	public void newTocSequence(TocProperties props) {
		tocSequence = new TocSequenceEventImpl(props, tocs.get(props.getTocName()), props.getRange(), null);
		add(tocSequence);
	}

	public void newOnTocStart(Condition useWhen) {
		formatters.add(tocSequence.addTocStart(useWhen));
	}

	public void newOnTocStart() {
		formatters.add(tocSequence.addTocStart(null));
	}

	public void newOnVolumeStart(Condition useWhen) {
		formatters.add(tocSequence.addVolumeStartEvents(useWhen));
	}

	public void newOnVolumeStart() {
		formatters.add(tocSequence.addVolumeStartEvents(null));
	}

	public void newOnVolumeEnd(Condition useWhen) {
		formatters.add(tocSequence.addVolumeEndEvents(useWhen));
	}

	public void newOnVolumeEnd() {
		formatters.add(tocSequence.addVolumeEndEvents(null));
	}

	public void newOnTocEnd(Condition useWhen) {
		formatters.add(tocSequence.addTocEnd(useWhen));
	}

	public void newOnTocEnd() {
		formatters.add(tocSequence.addTocEnd(null));
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

	public void insertEvaluate(DynamicContent exp, TextProperties t) {
		current().insertEvaluate(exp, t);
	}

	@Override
	public void newItemSequence(ItemSequenceProperties props) {
		itemSequence = new ItemSequenceEventImpl(props, props.getRange(), props.getCollectionID());
		add(itemSequence);
	}

	@Override
	public void newOnCollectionStart() {
		formatters.add(itemSequence.addCollectionStart());
	}

	@Override
	public void newOnCollectionEnd() {
		formatters.add(itemSequence.addCollectionEnd());
	}

	@Override
	public void newOnPageStart() {
		formatters.add(itemSequence.addPageStartEvents());
	}

	@Override
	public void newOnPageEnd() {
		formatters.add(itemSequence.addPageEndEvents());
	}

}
