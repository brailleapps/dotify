package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockProperties;
import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.DynamicContent;
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
	private final VolumeTemplateImpl template;
	private List<FormatterCore> formatters;
	private TocSequenceEventImpl tocSequence;

	public VolumeContentBuilderImpl(VolumeTemplateImpl template) {
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
		tocSequence = new TocSequenceEventImpl(props, props.getTocName(), props.getRange(), props.getCondition(), null, template);
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

	public void insertEvaluate(DynamicContent exp, TextProperties t) {
		current().insertEvaluate(exp, t);
	}

}
