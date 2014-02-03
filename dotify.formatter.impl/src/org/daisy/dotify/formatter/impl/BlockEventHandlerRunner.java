package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.LayoutMaster;
import org.daisy.dotify.api.formatter.PageStruct;
import org.daisy.dotify.api.formatter.TocProperties;
import org.daisy.dotify.api.obfl.ExpressionFactory;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.tools.CompoundIterable;

class BlockEventHandlerRunner implements VolumeContentFormatter {
	private final Iterable<VolumeTemplateImpl> volumeTemplates;
	private final Map<String, LayoutMaster> masters;
	private final Map<String, TableOfContentsImpl> tocs;
	private final Logger logger;
	//private final FormatterFactory ff;
	private final BrailleTranslator bt;
	private final ExpressionFactory ef;
	
	public BlockEventHandlerRunner(Map<String, LayoutMaster> masters, Map<String, TableOfContentsImpl> tocs, Iterable<VolumeTemplateImpl> volumeTemplates, BrailleTranslator bt, ExpressionFactory ef) {
		this.volumeTemplates = volumeTemplates;
		this.masters = masters;
		this.tocs = tocs;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.bt = bt;
		//this.ff = ff;
		this.ef = ef;
	}
	
	private void appendToc(VolumeSequenceEvent seq, CrossReferences crh, int volumeNumber, int volumeCount, List<Iterable<BlockSequence>> ib, Map<String, String> vars) throws IOException {
		TocSequenceEvent toc = (TocSequenceEvent)seq;
		if (toc.appliesTo(vars)) {
			BlockEventHandler beh = new BlockEventHandler(masters, bt, ef);
			TableOfContentsImpl data = tocs.get(toc.getTocName());
			StaticSequenceEventImpl evs = new StaticSequenceEventImpl(toc.getSequenceProperties());
			for (BlockEvent e : toc.getTocStartEvents(vars)) {
				evs.push(e);
			}
			for (BlockEvent e : data) {
				evs.push(e);
			}
			if (toc.getRange()==TocProperties.TocRange.DOCUMENT) {
				for (BlockEvent e : toc.getVolumeEndEvents(vars)) {
					evs.push(e);
				}
			}
			for (BlockEvent e : toc.getTocEndEvents(vars)) {
				evs.push(e);
			}
			if (toc.getRange()==TocProperties.TocRange.VOLUME) {
				beh.formatSequence(evs, vars);
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
						BlockEventHandler beh2 = new BlockEventHandler(masters, bt, ef);
						StaticSequenceEventImpl evs2 = new StaticSequenceEventImpl(toc.getSequenceProperties());
						for (BlockEvent e : toc.getTocEndEvents(vars)) {
							evs2.add(e);
						}
						beh2.formatSequence(evs2, vars);
						fsm.appendGroup(beh2.close().getBlockSequenceIterable().iterator().next());
						r.add(fsm.newSequence());
						ib.add(r);
					} catch (Exception e) {
						logger.log(Level.SEVERE, "TOC failed for: volume " + volumeNumber + " of " + volumeCount, e);
					}
				}
			} else if (toc.getRange()==TocProperties.TocRange.DOCUMENT) {
				beh.formatSequence(evs, vars);
				BlockSequenceManipulator fsm = new BlockSequenceManipulator(beh.close());
				int nv=0;
				HashMap<String, BlockSequence> statics = new HashMap<String, BlockSequence>();
				for (Block b : fsm.getBlocks()) {
					if (b.getBlockIdentifier()!=null) {
						String ref = data.getRefForID(b.getBlockIdentifier());
						Integer vol = crh.getVolumeNumber(ref);
						if (vol!=null) {
							if (nv!=vol) {
								BlockEventHandler beh2 = new BlockEventHandler(masters, bt, ef);
								beh2.newSequence(toc.getSequenceProperties(), vars);
								if (nv>0) {
									vars.put(toc.getStartedVolumeVariableName(), ""+nv);
									beh2.formatBlock(toc.getVolumeEndEvents(vars), vars);
								}
								nv = vol;
								vars.put(toc.getStartedVolumeVariableName(), ""+vol);
								beh2.formatBlock(toc.getVolumeStartEvents(vars), vars);
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
		for (VolumeTemplateImpl t : volumeTemplates) {
			if (t==null) {
				System.out.println("VOLDATA NULL");
			}
			if (t.appliesTo(volumeNumber, volumeCount)) {
				return t.getVolumeMaxSize();
			}
		}
		//TODO: don't return a fixed value
		return 50;
	}
	
	
	public PageStruct formatPreVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh) {
		try {
			List<Iterable<BlockSequence>> ib = formatVolumeContents(volumeNumber, volumeCount, crh, true);
			PaginatorImpl paginator2 = new PaginatorImpl();
			paginator2.open(bt, new CompoundIterable<BlockSequence>(ib));
			PageStruct ret = paginator2.paginate(crh);
			paginator2.close();
			return ret;
		} catch (IOException e) {
			return null;
		} catch (PaginatorException e) {
			return null;
		}
	}
	
	public PageStruct formatPostVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh) {
		try {
			List<Iterable<BlockSequence>> ib = formatVolumeContents(volumeNumber, volumeCount, crh, false);
			PaginatorImpl paginator2 = new PaginatorImpl();
			paginator2.open(bt, new CompoundIterable<BlockSequence>(ib));
			PageStruct ret = paginator2.paginate(crh);
			paginator2.close();
			return ret;
		} catch (IOException e) {
			return null;
		} catch (PaginatorException e) {
			return null;
		}
	}
	
	private List<Iterable<BlockSequence>> formatVolumeContents(int volumeNumber, int volumeCount, CrossReferences crh, boolean pre) throws IOException {
		ArrayList<Iterable<BlockSequence>> ib = new ArrayList<Iterable<BlockSequence>>();
		for (VolumeTemplateImpl t : volumeTemplates) {
			if (t.appliesTo(volumeNumber, volumeCount)) {
				HashMap<String, String> vars = new HashMap<String, String>();
				vars.put(t.getVolumeCountVariableName(), volumeCount+"");
				vars.put(t.getVolumeNumberVariableName(), volumeNumber+"");
				for (VolumeSequenceEvent seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
					if (seq instanceof TocSequenceEvent) {
						appendToc(seq, crh, volumeNumber, volumeCount, ib, vars);
					} else if (seq instanceof SequenceEvent) {
						BlockEventHandler beh = new BlockEventHandler(masters, bt, ef);
						SequenceEvent seqEv = ((SequenceEvent)seq);
						beh.formatSequence(seqEv, vars);
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
