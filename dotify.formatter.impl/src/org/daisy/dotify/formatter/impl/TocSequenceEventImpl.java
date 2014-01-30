package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
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
	private final VolumeTemplateImpl template;
	private final String volEventVariable;
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
		this.template = template;
		this.volEventVariable = (volEventVar!=null?volEventVar:DEFAULT_EVENT_VOLUME_NUMBER);
		this.ef = ef;
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
	public boolean appliesTo(int volume, int volumeCount) {
		if (condition==null) {
			return true;
		}
		String[] vars = new String[] {
				template.getVolumeNumberVariableName()+"="+volume,
				template.getVolumeCountVariableName()+"="+volumeCount
			};
		return ef.newExpression().evaluate(condition, vars).equals(true);
	}

	/**
	 * Gets the TOC events 
	 * @param volume
	 * @param volumeCount
	 * @return returns the TOC events
	 */
	public TocEvents getTocEvents(int volume, int volumeCount) {
		return new TocEventsImpl(volume, volumeCount);
	}
	
	private static Iterable<BlockEvent> getCompoundIterable(Iterable<ConditionalEvents> events, Map<String, String> vars) {
		ArrayList<Iterable<BlockEvent>> it = new ArrayList<Iterable<BlockEvent>>();
		for (ConditionalEvents ev : events) {
			if (ev.appliesTo(vars)) {
				Iterable<BlockEvent> tmp = ev.getEvents();
				for (BlockEvent e : tmp) {
					e.setEvaluateContext(vars);
				}
				it.add(tmp);
			}
		}
		return new CompoundIterable<BlockEvent>(it);
	}

	private class TocEventsImpl implements TocEvents {
		private final HashMap<String, String> vars;
		
		public TocEventsImpl(int volume, int volumeCount) {
			vars = new HashMap<String, String>();
			vars.put(template.getVolumeNumberVariableName(), volume+"");
			vars.put(template.getVolumeCountVariableName(), volumeCount+"");
		}

		public Iterable<BlockEvent> getTocStartEvents() {
			return getCompoundIterable(tocStartEvents, vars);
		}

		public Iterable<BlockEvent> getVolumeStartEvents(int forVolume) {
			HashMap<String, String> v2 = new HashMap<String, String>();
			v2.putAll(vars);
			v2.put(volEventVariable, forVolume+"");
			return getCompoundIterable(volumeStartEvents, v2);
		}

		public Iterable<BlockEvent> getVolumeEndEvents(int forVolume) {
			HashMap<String, String> v2 = new HashMap<String, String>();
			v2.putAll(vars);
			v2.put(volEventVariable, forVolume+"");
			return getCompoundIterable(volumeEndEvents, v2);
		}

		public Iterable<BlockEvent> getTocEndEvents() {
			return getCompoundIterable(tocEndEvents, vars);
		}
	}

	public SequenceProperties getSequenceProperties() {
		return props;
	}
}
