package org.daisy.dotify.obfl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.book.VolumeContentFormatter;
import org.daisy.dotify.formatter.Block;
import org.daisy.dotify.formatter.BlockSequence;
import org.daisy.dotify.formatter.CrossReferences;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.obfl.TocSequenceEvent.TocRange;

class BlockEventHandlerRunner implements VolumeContentFormatter {
	private final Iterable<VolumeTemplate> volumeTemplates;
	private final FormatterFactory formatterFactory;
	private final Map<String, LayoutMaster> masters;
	private final Map<String, TableOfContents> tocs;
	private final Logger logger;
	
	BlockEventHandlerRunner(FormatterFactory formatterFactory, Map<String, LayoutMaster> masters, Map<String, TableOfContents> tocs, Iterable<VolumeTemplate> volumeTemplates) {
		this.volumeTemplates = volumeTemplates;
		this.formatterFactory = formatterFactory;
		this.masters = masters;
		this.tocs = tocs;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
	}
	
	private void appendToc(VolumeSequenceEvent seq, CrossReferences crh, int volumeNumber, int volumeCount, List<Iterable<BlockSequence>> ib) throws IOException {
		TocSequenceEvent toc = (TocSequenceEvent)seq;
		if (toc.appliesTo(volumeNumber, volumeCount)) {
			BlockEventHandler beh = new BlockEventHandler(formatterFactory, masters);
			TableOfContents data = tocs.get(toc.getTocName());
			TocEvents events = toc.getTocEvents(volumeNumber, volumeCount);
			StaticSequenceEventImpl evs = new StaticSequenceEventImpl(toc.getSequenceProperties());
			for (BlockEvent e : events.getTocStartEvents()) {
				evs.push(e);
			}
			for (BlockEvent e : data) {
				evs.push(e);
			}
			if (toc.getRange()==TocRange.DOCUMENT) {
				for (BlockEvent e : events.getVolumeEndEvents(volumeCount)) {
					evs.push(e);
				}
			}
			for (BlockEvent e : events.getTocEndEvents()) {
				evs.push(e);
			}
			if (toc.getRange()==TocRange.VOLUME) {
				beh.formatSequence(evs);
				BlockSequenceManipulator fsm = new BlockSequenceManipulator(beh.close());
				String start = null;
				String stop = null;
				//assumes toc is in sequential order
				for (String id : data.getTocIdList()) {
					String ref = data.getRefForID(id);
					int vol = crh.getVolumeNumber(ref);
					if (vol<volumeNumber) {
						
					} else if (vol==volumeNumber) {
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
						ArrayList<BlockSequence> r = new ArrayList<BlockSequence>();
						fsm.removeRange(data.getTocIdList().iterator().next(), start);
						fsm.removeTail(stop);
						BlockEventHandler beh2 = new BlockEventHandler(formatterFactory, masters);
						StaticSequenceEventImpl evs2 = new StaticSequenceEventImpl(toc.getSequenceProperties());
						for (BlockEvent e : events.getTocEndEvents()) {
							evs2.add(e);
						}
						beh2.formatSequence(evs2);
						fsm.appendGroup(beh2.close().getBlockSequenceIterable().iterator().next());
						r.add(fsm.newSequence());
						ib.add(r);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "TOC failed for: volume " + volumeNumber + " of " + volumeCount, e);
					}
				}
			} else if (toc.getRange()==TocRange.DOCUMENT) {
				beh.formatSequence(evs);
				BlockSequenceManipulator fsm = new BlockSequenceManipulator(beh.close());
				int nv=0;
				HashMap<String, BlockSequence> statics = new HashMap<String, BlockSequence>();
				for (Block b : fsm.getBlocks()) {
					if (b.getBlockIdentifier()!=null) {
						String ref = data.getRefForID(b.getBlockIdentifier());
						Integer vol = crh.getVolumeNumber(ref);
						if (vol!=null) {
							if (nv!=vol) {
								BlockEventHandler beh2 = new BlockEventHandler(formatterFactory, masters);
								StaticSequenceEventImpl evs2 = new StaticSequenceEventImpl(toc.getSequenceProperties());
								if (nv>0) {
									for (BlockEvent e : events.getVolumeEndEvents(nv)) {
										evs2.add(e);
									}
								}
								nv = vol;
								for (BlockEvent e : events.getVolumeStartEvents(vol)) {
									evs2.add(e);
								}
								beh2.formatSequence(evs2);
								statics.put(b.getBlockIdentifier(), beh2.close().getBlockSequenceIterable().iterator().next());
							}
						}
					}
				}
				for (String key : statics.keySet()) {
					fsm.insertGroup(statics.get(key), key);
				}
				ArrayList<BlockSequence> r = new ArrayList<BlockSequence>();
				r.add(fsm.newSequence());
				ib.add(r);
			} else {
				throw new RuntimeException("Coding error");
			}
		}
	}
	
	public int getVolumeMaxSize(int volumeNumber, int volumeCount) {
		for (VolumeTemplate t : volumeTemplates) {
			if (t==null) {
				System.out.println("VOLDATA NULL");
			}
			if (t.appliesTo(volumeNumber, volumeCount)) {
				return t.getVolumeMaxSize();
			}
		}
		return 50;
	}
	
	public List<Iterable<BlockSequence>> formatPreVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh) {
		try {
			return formatVolumeContents(volumeNumber, volumeCount, crh, true);
		} catch (IOException e) {
			return null;
		}
	}
	
	public List<Iterable<BlockSequence>> formatPostVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh) {
		try {
			return formatVolumeContents(volumeNumber, volumeCount, crh, false);
		} catch (IOException e) {
			return null;
		}
	}
	
	private List<Iterable<BlockSequence>> formatVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh, boolean pre) throws IOException {
		ArrayList<Iterable<BlockSequence>> ib = new ArrayList<Iterable<BlockSequence>>();
		for (VolumeTemplate t : volumeTemplates) {
			if (t.appliesTo(volumeNumber, volumeCount)) {
				for (VolumeSequenceEvent seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
					if (seq instanceof TocSequenceEvent) {
						appendToc(seq, crh, volumeNumber, volumeCount, ib);
					} else if (seq instanceof SequenceEvent) {
						BlockEventHandler beh = new BlockEventHandler(formatterFactory, masters);
						SequenceEvent seqEv = ((SequenceEvent)seq);
						HashMap<String, String> vars = new HashMap<String, String>();
						vars.put(t.getVolumeCountVariableName(), volumeCount+"");
						vars.put(t.getVolumeNumberVariableName(), volumeNumber+"");
						seqEv.setEvaluateContext(vars);
						beh.formatSequence(seqEv);
						ib.add(beh.close().getBlockSequenceIterable());
					} else {
						throw new RuntimeException("Unexpected error");
					}
				}
				break;
			}
		}
		return ib;
	}

}
