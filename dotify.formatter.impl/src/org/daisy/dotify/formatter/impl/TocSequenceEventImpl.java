package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Map;

import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.tools.CompoundIterable;

class TocSequenceEventImpl implements TocSequenceEvent {

	public final static String DEFAULT_EVENT_VOLUME_NUMBER = "started-volume-number";
	private final SequenceProperties props;
	private final String tocName;
	private final TocProperties.TocRange range;
	private final String condition;
	private final ArrayList<ConditionalEvents> tocStartEvents;
	private final ArrayList<ConditionalEvents> volumeStartEvents;
	private final ArrayList<ConditionalEvents> volumeEndEvents;
	private final ArrayList<ConditionalEvents> tocEndEvents;
	private final String startedVolumeVariableName;
	private final ExpressionFactory ef;
	
	public TocSequenceEventImpl(SequenceProperties props, String tocName, TocProperties.TocRange range, String condition, String volEventVar, VolumeTemplateImpl template, ExpressionFactory ef) {
		this.props = props;
		this.tocName = tocName;
		this.range = range;
		this.condition = condition;
		this.tocStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeEndEvents = new ArrayList<ConditionalEvents>();
		this.tocEndEvents = new ArrayList<ConditionalEvents>();
		this.startedVolumeVariableName = (volEventVar!=null?volEventVar:DEFAULT_EVENT_VOLUME_NUMBER);
		this.ef = ef;
	}
	
	public String getStartedVolumeVariableName() {
		return startedVolumeVariableName;
	}

	void addTocStartEvents(FormatterCoreEventImpl events, String condition) {
		tocStartEvents.add(new ConditionalEvents(events, condition, ef));
	}

	void addVolumeStartEvents(FormatterCoreEventImpl events, String condition) {
		volumeStartEvents.add(new ConditionalEvents(events, condition, ef));
	}
	
	void addVolumeEndEvents(FormatterCoreEventImpl events, String condition) {
		volumeEndEvents.add(new ConditionalEvents(events, condition, ef));
	}
	
	void addTocEndEvents(FormatterCoreEventImpl events, String condition) {
		tocEndEvents.add(new ConditionalEvents(events, condition, ef));
	}

	public VolumeSequenceType getVolumeSequenceType() {
		return VolumeSequenceType.TABLE_OF_CONTENTS;
	}

	public String getTocName() {
		return tocName;
	}

	public TocProperties.TocRange getRange() {
		return range;
	}

	/**
	 * Returns true if this toc sequence applies to the supplied context
	 * @param volume
	 * @param volumeCount
	 * @return returns true if this toc sequence applies to the supplied context, false otherwise
	 */
	public boolean appliesTo(Map<String, String> vars) {
		if (condition==null) {
			return true;
		}
		return ef.newExpression().evaluate(condition, vars).equals(true);
	}
	
	private static Iterable<BlockEvent> getCompoundIterable(Iterable<ConditionalEvents> events, Map<String, String> vars) {
		ArrayList<Iterable<BlockEvent>> it = new ArrayList<Iterable<BlockEvent>>();
		for (ConditionalEvents ev : events) {
			if (ev.appliesTo(vars)) {
				Iterable<BlockEvent> tmp = ev.getEvents();
				it.add(tmp);
			}
		}
		return new CompoundIterable<BlockEvent>(it);
	}

	public Iterable<BlockEvent> getTocStartEvents(Map<String, String> vars) {
		return getCompoundIterable(tocStartEvents, vars);
	}

	public Iterable<BlockEvent> getVolumeStartEvents(Map<String, String> vars) {
		return getCompoundIterable(volumeStartEvents, vars);
	}

	public Iterable<BlockEvent> getVolumeEndEvents(Map<String, String> vars) {
		return getCompoundIterable(volumeEndEvents, vars);
	}

	public Iterable<BlockEvent> getTocEndEvents(Map<String, String> vars) {
		return getCompoundIterable(tocEndEvents, vars);
	}

	public SequenceProperties getSequenceProperties() {
		return props;
	}
}
