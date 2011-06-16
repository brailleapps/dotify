package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.formatter.BlockEvent;
import org.daisy.dotify.formatter.ConditionalEvents;
import org.daisy.dotify.formatter.SequenceProperties;
import org.daisy.dotify.formatter.TocEvents;
import org.daisy.dotify.formatter.TocSequenceEvent;
import org.daisy.dotify.formatter.utils.Expression;
import org.daisy.dotify.tools.CompoundIterable;

public class TocSequenceEventImpl implements TocSequenceEvent {
	private final SequenceProperties props;
	private final String tocName;
	private final TocRange range;
	private final String condition;
	private final ArrayList<ConditionalEvents> tocStartEvents;
	private final ArrayList<ConditionalEvents> volumeStartEvents;
	private final ArrayList<ConditionalEvents> volumeEndEvents;
	private final ArrayList<ConditionalEvents> tocEndEvents;
	
	
	public TocSequenceEventImpl(SequenceProperties props, String tocName, TocRange range, String condition) {
		this.props = props;
		this.tocName = tocName;
		this.range = range;
		this.condition = condition;
		this.tocStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeEndEvents = new ArrayList<ConditionalEvents>();
		this.tocEndEvents = new ArrayList<ConditionalEvents>();
	}
	
	public void addTocStartEvents(Iterable<BlockEvent> events, String condition) {
		tocStartEvents.add(new ConditionalEvents(events, condition));
	}

	public void addVolumeStartEvents(Iterable<BlockEvent> events, String condition) {
		volumeStartEvents.add(new ConditionalEvents(events, condition));
	}
	
	public void addVolumeEndEvents(Iterable<BlockEvent> events, String condition) {
		volumeEndEvents.add(new ConditionalEvents(events, condition));
	}
	
	public void addTocEndEvents(Iterable<BlockEvent> events, String condition) {
		tocEndEvents.add(new ConditionalEvents(events, condition));
	}

	public Type getType() {
		return Type.TABLE_OF_CONTENTS;
	}

	public String getTocName() {
		return tocName;
	}

	public TocRange getRange() {
		return range;
	}

	public boolean appliesTo(int volume, int volumeCount) {
		if (condition==null) {
			return true;
		}
		HashMap<String, String> vars = new HashMap<String, String>();
		//FIXME: add in some other way
		vars.put("volume", volume+"");
		vars.put("volumes", volumeCount+"");
		return new Expression().evaluate(condition, vars).equals(true);
	}

	public TocEvents getTocEvents(int volume, int volumeCount) {
		return new TocEventsImpl(volume, volumeCount);
	}
	
	private class TocEventsImpl implements TocEvents {
		private final HashMap<String, String> vars;
		
		public TocEventsImpl(int volume, int volumeCount) {
			vars = new HashMap<String, String>();
			//FIXME:
			vars.put("volume", volume+"");
		}
		
		private Iterable<BlockEvent> getCompoundIterable(Iterable<ConditionalEvents> events, Map<String, String> vars) {
			ArrayList<Iterable<BlockEvent>> it = new ArrayList<Iterable<BlockEvent>>();
			for (ConditionalEvents ev : events) {
				if (ev.appliesTo(vars)) {
					it.add(ev.getEvents());
				}
			}
			return new CompoundIterable<BlockEvent>(it);
		}

		public Iterable<BlockEvent> getTocStartEvents() {
			return getCompoundIterable(tocStartEvents, vars);
		}

		public Iterable<BlockEvent> getVolumeStartEvents(int forVolume) {
			//FIXME:
			vars.put("started-volume-number", forVolume+"");
			return getCompoundIterable(volumeStartEvents, vars);
		}

		public Iterable<BlockEvent> getVolumeEndEvents(int forVolume) {
			//FIXME:
			vars.put("started-volume-number", forVolume+"");
			return getCompoundIterable(volumeEndEvents, vars);
		}

		public Iterable<BlockEvent> getTocEndEvents() {
			return getCompoundIterable(tocEndEvents, vars);
		}
		
	}

	public SequenceProperties getSequenceProperties() {
		return props;
	}

}