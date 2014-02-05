package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.tools.CompoundIterable;

class TocSequenceEventImpl implements TocSequenceEvent {

	public final static String DEFAULT_EVENT_VOLUME_NUMBER = "started-volume-number";
	private final SequenceProperties props;
	private final String tocName;
	private final TocProperties.TocRange range;
	private final Condition condition;
	private final ArrayList<ConditionalEvents> tocStartEvents;
	private final ArrayList<ConditionalEvents> volumeStartEvents;
	private final ArrayList<ConditionalEvents> volumeEndEvents;
	private final ArrayList<ConditionalEvents> tocEndEvents;
	private final String startedVolumeVariableName;
	
	public TocSequenceEventImpl(SequenceProperties props, String tocName, TocProperties.TocRange range, Condition condition, String volEventVar, VolumeTemplateImpl template) {
		this.props = props;
		this.tocName = tocName;
		this.range = range;
		this.condition = condition;
		this.tocStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeEndEvents = new ArrayList<ConditionalEvents>();
		this.tocEndEvents = new ArrayList<ConditionalEvents>();
		this.startedVolumeVariableName = (volEventVar!=null?volEventVar:DEFAULT_EVENT_VOLUME_NUMBER);
	}
	
	public String getStartedVolumeVariableName() {
		return startedVolumeVariableName;
	}

	void addTocStartEvents(FormatterCoreEventImpl events, Condition condition) {
		tocStartEvents.add(new ConditionalEvents(events, condition));
	}

	void addVolumeStartEvents(FormatterCoreEventImpl events, Condition condition) {
		volumeStartEvents.add(new ConditionalEvents(events, condition));
	}
	
	void addVolumeEndEvents(FormatterCoreEventImpl events, Condition condition) {
		volumeEndEvents.add(new ConditionalEvents(events, condition));
	}
	
	void addTocEndEvents(FormatterCoreEventImpl events, Condition condition) {
		tocEndEvents.add(new ConditionalEvents(events, condition));
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
	public boolean appliesTo(Context context) {
		if (condition==null) {
			return true;
		}
		return condition.evaluate(context);
	}
	
	private static Iterable<BlockEvent> getCompoundIterable(Iterable<ConditionalEvents> events, Context vars) {
		ArrayList<Iterable<BlockEvent>> it = new ArrayList<Iterable<BlockEvent>>();
		for (ConditionalEvents ev : events) {
			if (ev.appliesTo(vars)) {
				Iterable<BlockEvent> tmp = ev.getEvents();
				it.add(tmp);
			}
		}
		return new CompoundIterable<BlockEvent>(it);
	}

	public Iterable<BlockEvent> getTocStartEvents(Context vars) {
		return getCompoundIterable(tocStartEvents, vars);
	}

	public Iterable<BlockEvent> getVolumeStartEvents(Context vars) {
		return getCompoundIterable(volumeStartEvents, vars);
	}

	public Iterable<BlockEvent> getVolumeEndEvents(Context vars) {
		return getCompoundIterable(volumeEndEvents, vars);
	}

	public Iterable<BlockEvent> getTocEndEvents(Context vars) {
		return getCompoundIterable(tocEndEvents, vars);
	}

	public SequenceProperties getSequenceProperties() {
		return props;
	}
}
