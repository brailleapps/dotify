package org.daisy.dotify.formatter.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.daisy.dotify.formatter.FormatterException;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.PaginatorFactory;
import org.daisy.dotify.formatter.dom.Block;
import org.daisy.dotify.formatter.dom.BlockEvent;
import org.daisy.dotify.formatter.dom.BlockSequence;
import org.daisy.dotify.formatter.dom.BlockStruct;
import org.daisy.dotify.formatter.dom.BookStruct;
import org.daisy.dotify.formatter.dom.CrossReferences;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.PageStruct;
import org.daisy.dotify.formatter.dom.SequenceEvent;
import org.daisy.dotify.formatter.dom.TocEvents;
import org.daisy.dotify.formatter.dom.TocSequenceEvent;
import org.daisy.dotify.formatter.dom.TocSequenceEvent.TocRange;
import org.daisy.dotify.formatter.dom.VolumeSequenceEvent;
import org.daisy.dotify.formatter.dom.VolumeStruct;
import org.daisy.dotify.formatter.dom.VolumeTemplate;
import org.daisy.dotify.tools.CompoundIterable;

/**
 * Provides a default implementation of BookStruct
 * 
 * @author Joel Håkansson
 */
public class BookStructImpl implements BookStruct, CrossReferences {
	private final Logger logger;
	private final BlockStruct bs;
	private PageStruct ps;
	private final Map<String, LayoutMaster> masters;
	private final Iterable<VolumeTemplate> volumeTemplates;
	private final Map<String, TableOfContents> tocs;
	private final FormatterFactory formatterFactory;
	private final PaginatorFactory paginatorFactory;
	private Map<Page, Integer> pageSheetMap;
	private final Map<Integer, PageStruct> volPreStructMap;
	private final Map<Integer, PageStruct> volPostStructMap;
	private final Map<String, Integer> volLocations;
	private final Map<String, Integer> pageLocations;
	private boolean isDirty;
	private VolumeStruct volumeData;
	
	public BookStructImpl(BlockStruct bs, Map<String, LayoutMaster> masters, Iterable<VolumeTemplate> volumeTemplates, Map<String, TableOfContents> tocs,
			FormatterFactory factory, PaginatorFactory paginatorFactory) throws FormatterException {
		this.bs = bs;
		this.formatterFactory = factory;
		this.paginatorFactory = paginatorFactory;

		this.masters = masters;
		this.volumeTemplates = volumeTemplates;
		this.tocs = tocs;
		
		this.logger = Logger.getLogger(BookStructImpl.class.getCanonicalName());
		
		this.volPreStructMap = new HashMap<Integer, PageStruct>();
		this.volPostStructMap = new HashMap<Integer, PageStruct>();
		this.volLocations = new HashMap<String, Integer>();
		this.pageLocations = new HashMap<String, Integer>();
		this.isDirty = false;

		reformat();
	}
	
	public void reformat() throws FormatterException {
		Paginator paginator = paginatorFactory.newPaginator();
		paginator.open(formatterFactory);

		try {
			paginator.paginate(bs.getBlockSequenceIterable(), this);
			paginator.close();
		} catch (IOException e) {
			throw new FormatterException(e);
		}
		
		this.ps = paginator.getPageStruct();
		
		int sheetIndex=0;
		this.pageSheetMap = new HashMap<Page, Integer>();
		for (PageSequence s : this.ps.getContents()) {
			LayoutMaster lm = s.getLayoutMaster();
			int pageIndex=0;
			for (Page p : s) {
				if (!lm.duplex() || pageIndex%2==0) {
					sheetIndex++;
				}
				pageSheetMap.put(p, sheetIndex);
				pageIndex++;
			}
		}
	}
	
	public void setVolumeStruct(VolumeStruct volumeStruct) {
		this.volumeData = volumeStruct;
	}

	public PageStruct getPreVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, true);
	}

	public PageStruct getPostVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, false);
	}
	
	private PageStruct getVolumeContents(int volumeNumber, boolean pre) {
		try {
			ArrayList<Iterable<BlockSequence>> ib = new ArrayList<Iterable<BlockSequence>>();
			for (VolumeTemplate t : volumeTemplates) {
				if (t.appliesTo(volumeNumber, volumeData.getVolumeCount())) {
					for (VolumeSequenceEvent seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
						switch (seq.getType()) {
							case TABLE_OF_CONTENTS: {
								TocSequenceEvent toc = (TocSequenceEvent)seq;
								if (toc.appliesTo(volumeNumber, volumeData.getVolumeCount())) {
									BlockEventHandler beh = new BlockEventHandler(formatterFactory, masters);
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
										BlockSequenceManipulator fsm = new BlockSequenceManipulator(beh.close());
										String start = null;
										String stop = null;
										//assumes toc is in sequential order
										for (String id : data.getTocIdList()) {
											String ref = data.getRefForID(id);
											int vol = getVolumeNumber(ref);
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
												SequenceEventImpl evs2 = new SequenceEventImpl(toc.getSequenceProperties());
												for (BlockEvent e : events.getTocEndEvents()) {
													evs2.add(e);
												}
												beh2.formatSequence(evs2);
												fsm.appendGroup(beh2.close().getBlockSequenceIterable().iterator().next());
												r.add(fsm.newSequence());
												ib.add(r);
											} catch (Exception e) {
												logger.log(Level.SEVERE, "TOC failed for: volume " + volumeNumber + " of " + volumeData.getVolumeCount(), e);
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
												Integer vol = getVolumeNumber(ref);
												if (vol!=null) {
													if (nv!=vol) {
														BlockEventHandler beh2 = new BlockEventHandler(formatterFactory, masters);
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
								BlockEventHandler beh = new BlockEventHandler(formatterFactory, masters);
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
			Paginator paginator2 = paginatorFactory.newPaginator();
			paginator2.open(formatterFactory);
			CompoundIterable<BlockSequence> ci = new CompoundIterable<BlockSequence>(ib);
			paginator2.paginate(ci, this);
			paginator2.close();
			PageStruct ret = paginator2.getPageStruct();
			if (pre) {
				volPreStructMap.put(volumeNumber, ret);
			} else {
				volPostStructMap.put(volumeNumber, ret);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private int verifyVolumeLocation(String refid, int vol) {
		Integer v = volLocations.get(refid);
		volLocations.put(refid, vol);
		if (v!=null && v!=vol) {
			//this refid has been requested before and it changed location
			isDirty = true;
		}
		return vol;
	}
	
	private Page verifyPageLocation(String refid, Page page) {
		Integer p = pageLocations.get(refid);
		pageLocations.put(refid, page.getPageIndex());
		if (p!=null && p!=page.getPageIndex()) {
			//this refid has been requested before and it changed location
			isDirty = true;
		}
		return page;
	}

	public Integer getVolumeNumber(String refid) {
		for (int i=1; i<=volumeData.getVolumeCount(); i++) {
			if (volPreStructMap.get(i)!=null && volPreStructMap.get(i).getPage(refid)!=null) {
				return verifyVolumeLocation(refid, i);
			}
			if (volPostStructMap.get(i)!=null &&  volPostStructMap.get(i).getPage(refid)!=null) {
				return verifyVolumeLocation(refid, i);
			}
		}
		Integer i = pageSheetMap.get(getPage(refid));
		if (i!=null) {
			return verifyVolumeLocation(refid, volumeData.getVolumeForContentSheet(i));
		}
		isDirty = true;
		return null;
	}

	public PageStruct getContentsPageStruct() {
		return ps;
	}

	public Page getPage(String refid) {
		Page ret;
		if (ps!=null && (ret=ps.getPage(refid))!=null) {
			return verifyPageLocation(refid, ret);
		}
		if (volumeData!=null) {
			for (int i=1; i<=volumeData.getVolumeCount(); i++) {
				if (volPreStructMap.get(i)!=null && (ret=volPreStructMap.get(i).getPage(refid))!=null) {
					return verifyPageLocation(refid, ret);
				}
				if (volPostStructMap.get(i)!=null &&  (ret=volPostStructMap.get(i).getPage(refid))!=null) {
					return verifyPageLocation(refid, ret);
				}
			}
		}
		isDirty = true;
		return null;
	}

	public boolean isDirty() {
		return isDirty;
	}

	public void resetDirty() {
		isDirty = false;
		try {
			reformat();
		} catch (FormatterException e) {
			throw new RuntimeException("Error while reformatting.");
		}
	}
}
