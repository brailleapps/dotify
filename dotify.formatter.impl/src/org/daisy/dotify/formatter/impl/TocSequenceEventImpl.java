package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.tools.CompoundIterable;

class TocSequenceEventImpl implements VolumeSequenceEvent {
	private final SequenceProperties props;
	private final String tocName;
	private final TocProperties.TocRange range;
	private final Condition condition;
	private final ArrayList<ConditionalEvents> tocStartEvents;
	private final ArrayList<ConditionalEvents> volumeStartEvents;
	private final ArrayList<ConditionalEvents> volumeEndEvents;
	private final ArrayList<ConditionalEvents> tocEndEvents;
	
	public TocSequenceEventImpl(SequenceProperties props, String tocName, TocProperties.TocRange range, Condition condition, String volEventVar, VolumeTemplateImpl template) {
		this.props = props;
		this.tocName = tocName;
		this.range = range;
		this.condition = condition;
		this.tocStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeStartEvents = new ArrayList<ConditionalEvents>();
		this.volumeEndEvents = new ArrayList<ConditionalEvents>();
		this.tocEndEvents = new ArrayList<ConditionalEvents>();
	}

	FormatterCore addTocStartEvents(Condition condition) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocStartEvents.add(new ConditionalEvents(f, condition));
		return f;
	}

	FormatterCore addVolumeStartEvents(Condition condition) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		volumeStartEvents.add(new ConditionalEvents(f, condition));
		return f;
	}
	
	FormatterCore addVolumeEndEvents(Condition condition) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		volumeEndEvents.add(new ConditionalEvents(f, condition));
		return f;
	}
	
	FormatterCore addTocEndEvents(Condition condition) {
		FormatterCoreEventImpl f = new FormatterCoreEventImpl();
		tocEndEvents.add(new ConditionalEvents(f, condition));
		return f;
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

	/**
	 * Gets the events that should precede TOC entries from the specified volume 
	 * @param forVolume the number of the volume that is to be started, one based
	 * @return returns the events that should precede the TOC entries from the specified volume
	 */
	public Iterable<BlockEvent> getVolumeStartEvents(Context vars) {
		return getCompoundIterable(volumeStartEvents, vars);
	}

	/**
	 * Gets the events that should follow TOC entries from the specified volume
	 * @param forVolume the number of the volume that has just ended, one based
	 * @return returns the events that should follow the TOC entries from the specified volume
	 */
	public Iterable<BlockEvent> getVolumeEndEvents(Context vars) {
		return getCompoundIterable(volumeEndEvents, vars);
	}
	
	public BlockSequence getTocSequence(ArrayList<ConditionalEvents> evs, Context vars, FormatterContext context) throws IOException {
		BlockEventHandler beh2 = new BlockEventHandler(context);
		StaticSequenceEventImpl evs2 = new StaticSequenceEventImpl(getSequenceProperties());
		for (BlockEvent e : getCompoundIterable(evs, vars)) {
			evs2.add(e);
		}
		beh2.formatSequence(evs2, vars);
		return beh2.close().getBlockSequenceIterable().iterator().next();
	}
	
	public BlockSequence getVolumeStart(Context vars, FormatterContext context) throws IOException {
		return getTocSequence(volumeStartEvents, vars, context);
	}
	
	public BlockSequence getVolumeEnd(Context vars, FormatterContext context) throws IOException {
		return getTocSequence(volumeEndEvents, vars, context);
	}
	
	public BlockSequence getTocStart(Context vars, FormatterContext context) throws IOException {
		return getTocSequence(tocStartEvents, vars, context);
	}

	public BlockSequence getTocEnd(Context vars, FormatterContext context) throws IOException {
		return getTocSequence(tocEndEvents, vars, context);
	}

	public SequenceProperties getSequenceProperties() {
		return props;
	}

	public List<BlockSequence> getBlockSequence(FormatterContext context, DefaultContext vars, CrossReferences crh) {
		ArrayList<BlockSequence> r = new ArrayList<BlockSequence>();
		try {
		if (appliesTo(vars)) {

			TableOfContentsImpl data = context.getTocs().get(getTocName());
			BlockSequenceManipulator fsm = new BlockSequenceManipulator(
					context.getMasters().get(getSequenceProperties().getMasterName()), 
					getSequenceProperties().getInitialPageNumber());

			fsm.appendGroup(getTocStart(vars, context));
			BlockSequence d;
			{
				
				StaticSequenceEventImpl evs = new StaticSequenceEventImpl(getSequenceProperties());
				for (BlockEvent e : data) {
					evs.push(e);
				}

				d = evs.getBlockSequence(context, vars, crh).get(0);
			}

			if (getRange()==TocProperties.TocRange.DOCUMENT) {
				fsm.appendGroup(getVolumeEnd(vars, context));
			}

			fsm.appendGroup(d);
			fsm.appendGroup(getTocEnd(vars, context));

			if (getRange()==TocProperties.TocRange.VOLUME) {

				String start = null;
				String stop = null;
				//assumes toc is in sequential order
				for (String id : data.getTocIdList()) {
					String ref = data.getRefForID(id);
					int vol = crh.getVolumeNumber(ref);
					if (vol<vars.getCurrentVolume()) {
						
					} else if (vol==vars.getCurrentVolume()) {
						if (start==null) {
							start = id;
						}
						stop = id;
					} else {
						break;
					}
				}
				// start/stop can be null if no entries are in that volume
				if (start!=null && stop!=null) {
					try {
						fsm.removeRange(data.getTocIdList().iterator().next(), start);
						fsm.removeTail(stop);
						fsm.appendGroup(getTocEnd(vars, context));
						r.add(fsm.newSequence());
					} catch (Exception e) {
						Logger.getLogger(this.getClass().getCanonicalName()).
							log(Level.SEVERE, "TOC failed for: volume " + vars.getCurrentVolume() + " of " + vars.getVolumeCount(), e);
					}
				}
			} else if (getRange()==TocProperties.TocRange.DOCUMENT) {

				int nv=0;
				HashMap<String, BlockSequence> statics = new HashMap<String, BlockSequence>();
				for (Block b : fsm.getBlocks()) {
					if (b.getBlockIdentifier()!=null) {
						String ref = data.getRefForID(b.getBlockIdentifier());
						Integer vol = crh.getVolumeNumber(ref);
						if (vol!=null) {
							if (nv!=vol) {
								BlockEventHandler beh2 = new BlockEventHandler(context);
								beh2.newSequence(getSequenceProperties(), vars);
								if (nv>0) {
									vars.setMetaVolume(nv);
									beh2.formatBlock(getVolumeEndEvents(vars), vars);
								}
								nv = vol;
								vars.setMetaVolume(vol);
								beh2.formatBlock(getVolumeStartEvents(vars), vars);
								statics.put(b.getBlockIdentifier(), beh2.close().getBlockSequenceIterable().iterator().next());
							}
						}
					}
				}
				for (String key : statics.keySet()) {
					fsm.insertGroup(statics.get(key), key);
				}
				r.add(fsm.newSequence());
			} else {
				throw new RuntimeException("Coding error");
			}
		}
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Failed to assemble toc.", e);
		}
		return r;
	}
}
