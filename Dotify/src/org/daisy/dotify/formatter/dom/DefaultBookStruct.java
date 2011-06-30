package org.daisy.dotify.formatter.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.PaginatorFactory;
import org.daisy.dotify.formatter.PaginatorHandler;
import org.daisy.dotify.formatter.dom.TocSequenceEvent.TocRange;
import org.daisy.dotify.tools.CompoundIterable;

/**
 * Provides a default implementation of BookStruct
 * 
 * @author Joel Håkansson
 */
public class DefaultBookStruct implements BookStruct {
	private final Logger logger;
	private final PageStruct ps;
	private final Map<String, LayoutMaster> masters;
	private final Iterable<VolumeTemplate> volumeTemplates;
	private final Map<String, TableOfContents> tocs;
	private final FormatterFactory formatterFactory;
	
	public DefaultBookStruct(PageStruct ps, Map<String, LayoutMaster> masters, Iterable<VolumeTemplate> volumeTemplates, Map<String, TableOfContents> tocs,
			FormatterFactory factory) {
		this.ps = ps;
		this.masters = masters;
		this.volumeTemplates = volumeTemplates;
		this.tocs = tocs;
		this.formatterFactory = factory;
		this.logger = Logger.getLogger(DefaultBookStruct.class.getCanonicalName());
	}

	public Iterable<PageSequence> getPreVolumeContents(int volumeNumber, VolumeStruct volumeData) {
		return getVolumeContents(volumeNumber, volumeData, true);
	}

	public Iterable<PageSequence> getPostVolumeContents(int volumeNumber, VolumeStruct volumeData) {
		return getVolumeContents(volumeNumber, volumeData, false);
	}
	
	private Iterable<PageSequence> getVolumeContents(int volumeNumber, VolumeStruct volumeData, boolean pre) {
		try {
				ArrayList<Iterable<BlockSequence>> ib = new ArrayList<Iterable<BlockSequence>>();
				for (VolumeTemplate t : volumeTemplates) {
					if (t.appliesTo(volumeNumber, volumeData.getVolumeCount())) {
						for (VolumeSequence seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
							switch (seq.getType()) {
								case TABLE_OF_CONTENTS: {
									TocSequenceEvent toc = (TocSequenceEvent)seq;
									if (toc.appliesTo(volumeNumber, volumeData.getVolumeCount())) {
										BlockEventHandler beh = new BlockEventHandler(formatterFactory, masters, this);
										HashMap<Integer, Integer> pageSheetMap = new HashMap<Integer, Integer>();
										int page=0;
										int sheetIndex=0;
										for (PageSequence s : this.getPageStruct().getContents()) {
											LayoutMaster lm = s.getLayoutMaster();
											for (@SuppressWarnings("unused") Page p : s) {
												if (!lm.duplex() || page%2==0) {
													sheetIndex++;
												}
												pageSheetMap.put(page, sheetIndex);
												page++;
											}
										}
										TableOfContents data = tocs.get(toc.getTocName());
										TocEvents events = toc.getTocEvents(volumeNumber, volumeData.getVolumeCount());
										SequenceEventImpl evs = new SequenceEventImpl(toc.getSequenceProperties());
										for (BlockEvent e : events.getTocStartEvents()) {
											evs.push(e);
										}
										for (BlockEvent e : data) {
											evs.push(e);
										}
										if (toc.getRange()==TocRange.DOCUMENT) {
											for (BlockEvent e : events.getVolumeEndEvents(volumeData.getVolumeCount())) {
												evs.push(e);
											}
										}
										for (BlockEvent e : events.getTocEndEvents()) {
											evs.push(e);
										}
										if (toc.getRange()==TocRange.VOLUME) {
											beh.formatSequence(evs);
											FlowSequenceManipulator fsm = new FlowSequenceManipulator(beh.close());
											String start = null;
											String stop = null;
											//assumes toc is in sequential order
											for (String id : data.getTocIdList()) {
												String ref = data.getRefForID(id);
												Integer p = getPageNumber(ref);
												int vol = volumeData.getVolumeForContentSheet(pageSheetMap.get(p));
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
													//FIXME: does not keep toc-end events
													r.add(fsm.newSequenceFromHead(stop));
													ib.add(r);
												} catch (Exception e) {
													logger.log(Level.SEVERE, "TOC failed for: volume " + volumeNumber + " of " + volumeData.getVolumeCount(), e);
												}
											}
										} else if (toc.getRange()==TocRange.DOCUMENT) {
											beh.formatSequence(evs);
											FlowSequenceManipulator fsm = new FlowSequenceManipulator(beh.close());
											int nv=0;
											HashMap<String, BlockSequence> statics = new HashMap<String, BlockSequence>();
											for (Block b : fsm.getBlocks()) {
												if (b.getBlockIdentifier()!=null) {
													String ref = data.getRefForID(b.getBlockIdentifier());
													Integer p = getPageNumber(ref);
													Integer i = pageSheetMap.get(p);
													if (i!=null) {
														int vol = volumeData.getVolumeForContentSheet(i);
														if (nv!=vol) {
															BlockEventHandler beh2 = new BlockEventHandler(formatterFactory, masters, this);
															SequenceEventImpl evs2 = new SequenceEventImpl(toc.getSequenceProperties());
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
									break; }
								case STATIC: {
									BlockEventHandler beh = new BlockEventHandler(formatterFactory, masters, this);
									SequenceEvent seqEv = ((SequenceEvent)seq);
									HashMap<String, String> vars = new HashMap<String, String>();
									vars.put(t.getVolumeCountVariableName(), volumeData.getVolumeCount()+"");
									vars.put(t.getVolumeNumberVariableName(), volumeNumber+"");
									seqEv.setEvaluateContext(vars);
									beh.formatSequence(seqEv);
									ib.add(beh.close().getBlockSequenceIterable());
									break; }
								default:
									throw new RuntimeException("Unexpected error");
							}
						}
						break;
					}
				}
				Paginator paginator2 = PaginatorFactory.newInstance().newPaginator();
				paginator2.open(formatterFactory);
				CompoundIterable<BlockSequence> ci = new CompoundIterable<BlockSequence>(ib);
				PaginatorHandler.paginate(ci, paginator2);
				paginator2.close();
				return paginator2.getPageStruct().getContents();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
/*
	public Integer getVolumeNumber(String refid) {
		throw new RuntimeException("Not implemented");
	}
*/
	public PageStruct getPageStruct() {
		return ps;
	}

	public Integer getPageNumber(String refid) {
		return ps.getPageNumber(refid);
	}

}
