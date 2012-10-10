package org.daisy.dotify.obfl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.formatter.SequenceProperties;
import org.daisy.dotify.tools.CompoundIterable;

class TocSequenceEventImpl implements TocSequenceEvent {

	public final static String DEFAULT_EVENT_VOLUME_NUMBER = "started-volume-number";
	private final SequenceProperties props;
	private final String tocName;
	private final TocRange range;
	private final String condition;
	private final ArrayList<ConditionalEvents> tocStartEvents;
	private final ArrayList<ConditionalEvents> volumeStartEvents;
	private final ArrayList<ConditionalEvents> volumeEndEvents;
	private final ArrayList<ConditionalEvents> tocEndEvents;
	private final VolumeTemplate template;
	private final String volEventVariable;
	
	public TocSequenceEventImpl(SequenceProperties props, String tocName, TocRange range, String condition, String volEventVar, VolumeTemplate template) {
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
	}
	
	void addTocStartEvents(Iterable<BlockEvent> events, String condition) {
		tocStartEvents.add(new ConditionalEvents(events, condition));
	}

	void addVolumeStartEvents(Iterable<BlockEvent> events, String condition) {
		volumeStartEvents.add(new ConditionalEvents(events, condition));
	}
	
	void addVolumeEndEvents(Iterable<BlockEvent> events, String condition) {
		volumeEndEvents.add(new ConditionalEvents(events, condition));
	}
	
	void addTocEndEvents(Iterable<BlockEvent> events, String condition) {
		tocEndEvents.add(new ConditionalEvents(events, condition));
	}

	public VolumeSequenceType getVolumeSequenceType() {
		return VolumeSequenceType.TABLE_OF_CONTENTS;
	}

	public String getTocName() {
		return tocName;
	}

	public TocRange getRange() {
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
		return new Expression().evaluate(condition, vars).equals(true);
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
