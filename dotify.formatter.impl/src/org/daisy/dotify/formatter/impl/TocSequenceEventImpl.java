package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.Condition;
import org.daisy.dotify.api.formatter.Context;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TocProperties;

class TocSequenceEventImpl implements VolumeSequence {
	private final SequenceProperties props;
	private final TableOfContentsImpl data;
	private final TocProperties.TocRange range;
	private final ArrayList<ConditionalBlock> tocStartEvents;
	private final ArrayList<ConditionalBlock> volumeStartEvents;
	private final ArrayList<ConditionalBlock> volumeEndEvents;
	private final ArrayList<ConditionalBlock> tocEndEvents;
	
	public TocSequenceEventImpl(SequenceProperties props, TableOfContentsImpl data, TocProperties.TocRange range, String volEventVar) {
		this.props = props;
		this.data = data;
		this.range = range;
		this.tocStartEvents = new ArrayList<>();
		this.volumeStartEvents = new ArrayList<>();
		this.volumeEndEvents = new ArrayList<>();
		this.tocEndEvents = new ArrayList<>();
	}

	FormatterCore addTocStart(Condition condition) {
		// we don't need a layout master here, because it will be replaced before rendering below
		FormatterCoreImpl f = new FormatterCoreImpl();
		tocStartEvents.add(new ConditionalBlock(f, condition));
		return f;
	}

	FormatterCore addVolumeStartEvents(Condition condition) {
		FormatterCoreImpl f = new FormatterCoreImpl();
		volumeStartEvents.add(new ConditionalBlock(f, condition));
		return f;
	}
	
	FormatterCore addVolumeEndEvents(Condition condition) {
		FormatterCoreImpl f = new FormatterCoreImpl();
		volumeEndEvents.add(new ConditionalBlock(f, condition));
		return f;
	}
	
	FormatterCore addTocEnd(Condition condition) {
		// we don't need a layout master here, because it will be replaced before rendering below
		FormatterCoreImpl f = new FormatterCoreImpl();
		tocEndEvents.add(new ConditionalBlock(f, condition));
		return f;
	}

	public TocProperties.TocRange getRange() {
		return range;
	}

	private static Iterable<Block> getCompoundIterableB(Iterable<ConditionalBlock> events, Context vars) {
		ArrayList<Block> it = new ArrayList<>();
		for (ConditionalBlock ev : events) {
			if (ev.appliesTo(vars)) {
				Iterable<Block> tmp = ev.getSequence();
				for (Block b : tmp) {
					//always clone these blocks, as they may be placed in multiple contexts
					it.add((Block)b.clone());
				}
			}
		}
		return it;
	}

	public Iterable<Block> getVolumeStart(Context vars) throws IOException {
		return getCompoundIterableB(volumeStartEvents, vars);
	}
	
	public Iterable<Block> getVolumeEnd(Context vars) throws IOException {
		return getCompoundIterableB(volumeEndEvents, vars);
	}
	
	public Iterable<Block> getTocStart(Context vars) throws IOException {
		return getCompoundIterableB(tocStartEvents, vars);
	}

	public Iterable<Block> getTocEnd(Context vars) throws IOException {
		return getCompoundIterableB(tocEndEvents, vars);
	}

	@Override
	public SequenceProperties getSequenceProperties() {
		return props;
	}

	@Override
	public BlockSequence getBlockSequence(FormatterContext context, DefaultContext vars, CrossReferences crh) {
		try {
			BlockSequenceManipulator fsm = new BlockSequenceManipulator(
					context.getMasters().get(getSequenceProperties().getMasterName()), 
					getSequenceProperties().getInitialPageNumber());

			fsm.appendGroup(getTocStart(vars));

			fsm.appendGroup(data);
			
			if (getRange()==TocProperties.TocRange.DOCUMENT) {
				fsm.appendGroup(getVolumeEnd(vars));
			}

			fsm.appendGroup(getTocEnd(vars));

			if (getRange()==TocProperties.TocRange.VOLUME) {

				String start = null;
				String stop = null;
				//assumes toc is in sequential order
				for (String id : data.getTocIdList()) {
					String ref = data.getRefForID(id);
					Integer volNo = crh.getVolumeNumber(ref);
					
					int vol = (volNo!=null?volNo:1);
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
						fsm.appendGroup(getTocEnd(vars));
						return fsm.newSequence();
					} catch (Exception e) {
						Logger.getLogger(this.getClass().getCanonicalName()).
							log(Level.SEVERE, "TOC failed for: volume " + vars.getCurrentVolume() + " of " + vars.getVolumeCount(), e);
					}
				}
			} else if (getRange()==TocProperties.TocRange.DOCUMENT) {

				int nv=0;
				HashMap<String, Iterable<Block>> statics = new HashMap<>();
				for (Block b : fsm.getBlocks()) {
					if (b.getBlockIdentifier()!=null) {
						String ref = data.getRefForID(b.getBlockIdentifier());
						Integer vol = crh.getVolumeNumber(ref);
						if (vol!=null) {
							if (nv!=vol) {
								ArrayList<Block> rr = new ArrayList<>();
								if (nv>0) {
									Iterable<Block> ib1 = getVolumeEnd(DefaultContext.from(vars).metaVolume(nv).build());
									for (Block b1 : ib1) {
										//set the meta volume for each block, for later evaluation
										b1.setMetaVolume(nv);
										rr.add(b1);
									}
								}
								nv = vol;
								Iterable<Block> ib1 = getVolumeStart(DefaultContext.from(vars).metaVolume(vol).build());
								for (Block b1 : ib1) {
									//set the meta volume for each block, for later evaluation
									b1.setMetaVolume(vol);
									rr.add(b1);
								}
								statics.put(b.getBlockIdentifier(), rr);
							}
						}
					}
				}
				for (String key : statics.keySet()) {
					fsm.insertGroup(statics.get(key), key);
				}
				return fsm.newSequence();
			} else {
				throw new RuntimeException("Coding error");
			}
		} catch (IOException e) {
			Logger.getLogger(this.getClass().getCanonicalName()).log(Level.WARNING, "Failed to assemble toc.", e);
		}
		return null;
	}
}
