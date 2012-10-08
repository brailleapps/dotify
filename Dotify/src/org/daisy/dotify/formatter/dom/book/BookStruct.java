package org.daisy.dotify.formatter.dom.book;

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
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.PageStruct;
import org.daisy.dotify.formatter.dom.StaticSequenceEvent;
import org.daisy.dotify.formatter.dom.TocEvents;
import org.daisy.dotify.formatter.dom.TocSequenceEvent;
import org.daisy.dotify.formatter.dom.TocSequenceEvent.TocRange;
import org.daisy.dotify.formatter.dom.VolumeSequenceEvent;
import org.daisy.dotify.formatter.dom.block.Block;
import org.daisy.dotify.formatter.dom.block.BlockSequence;
import org.daisy.dotify.formatter.dom.block.BlockStruct;
import org.daisy.dotify.formatter.obfl.BlockEvent;
import org.daisy.dotify.formatter.obfl.BlockEventHandler;
import org.daisy.dotify.formatter.obfl.SequenceEvent;
import org.daisy.dotify.formatter.utils.PageTools;
import org.daisy.dotify.text.BreakPoint;
import org.daisy.dotify.text.BreakPointHandler;
import org.daisy.dotify.tools.CompoundIterable;
import org.daisy.dotify.writer.Volume;

/**
 * Provides a default implementation of BookStruct
 * 
 * @author Joel Håkansson
 */
public class BookStruct {
	private final static char ZERO_WIDTH_SPACE = '\u200b';
	private final Logger logger;
	private final BlockStruct bs;
	
	private final Map<String, LayoutMaster> masters;
	private final Iterable<VolumeTemplate> volumeTemplates;
	private final Map<String, TableOfContents> tocs;
	private final FormatterFactory formatterFactory;
	private final PaginatorFactory paginatorFactory;

	private final CrossReferenceHandler crh;

	//private VolumeStruct volumeData;

	public BookStruct(BlockStruct bs, Map<String, LayoutMaster> masters, Iterable<VolumeTemplate> volumeTemplates, Map<String, TableOfContents> tocs,
			FormatterFactory factory, PaginatorFactory paginatorFactory) throws FormatterException {
		this.bs = bs;
		this.formatterFactory = factory;
		this.paginatorFactory = paginatorFactory;

		this.masters = masters;
		this.volumeTemplates = volumeTemplates;
		this.tocs = tocs;
		
		this.logger = Logger.getLogger(BookStruct.class.getCanonicalName());

		this.crh = new CrossReferenceHandler();
	}
	
	private void reformat() throws FormatterException {
		Paginator paginator = paginatorFactory.newPaginator();
		paginator.open(formatterFactory);

		try {
			paginator.paginate(bs.getBlockSequenceIterable(), crh);
			paginator.close();
		} catch (IOException e) {
			throw new FormatterException(e);
		}

		crh.setContents(paginator.getPageStruct());
	}

	private PageStruct getPreVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, true);
	}

	private PageStruct getPostVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, false);
	}
	
	private PageStruct getVolumeContents(int volumeNumber, boolean pre) {
		try {
			ArrayList<Iterable<BlockSequence>> ib = new ArrayList<Iterable<BlockSequence>>();
			for (VolumeTemplate t : volumeTemplates) {
				if (t.appliesTo(volumeNumber, crh.getVolumeCount())) {
					for (VolumeSequenceEvent seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
						switch (seq.getVolumeSequenceType()) {
							case TABLE_OF_CONTENTS: {
								TocSequenceEvent toc = (TocSequenceEvent)seq;
								if (toc.appliesTo(volumeNumber, crh.getVolumeCount())) {
									BlockEventHandler beh = new BlockEventHandler(formatterFactory, masters);
									TableOfContents data = tocs.get(toc.getTocName());
									TocEvents events = toc.getTocEvents(volumeNumber, crh.getVolumeCount());
									StaticSequenceEvent evs = new StaticSequenceEvent(toc.getSequenceProperties());
									for (BlockEvent e : events.getTocStartEvents()) {
										evs.push(e);
									}
									for (BlockEvent e : data) {
										evs.push(e);
									}
									if (toc.getRange()==TocRange.DOCUMENT) {
										for (BlockEvent e : events.getVolumeEndEvents(crh.getVolumeCount())) {
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
												StaticSequenceEvent evs2 = new StaticSequenceEvent(toc.getSequenceProperties());
												for (BlockEvent e : events.getTocEndEvents()) {
													evs2.add(e);
												}
												beh2.formatSequence(evs2);
												fsm.appendGroup(beh2.close().getBlockSequenceIterable().iterator().next());
												r.add(fsm.newSequence());
												ib.add(r);
											} catch (Exception e) {
												logger.log(Level.SEVERE, "TOC failed for: volume " + volumeNumber + " of " + crh.getVolumeCount(), e);
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
														StaticSequenceEvent evs2 = new StaticSequenceEvent(toc.getSequenceProperties());
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
								vars.put(t.getVolumeCountVariableName(), crh.getVolumeCount()+"");
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
			paginator2.paginate(ci, crh);
			paginator2.close();
			PageStruct ret = paginator2.getPageStruct();
			if (pre) {
				VolData d = crh.getVolData(volumeNumber);
				if (d==null) {
					d = new VolData();
					crh.setVolData(volumeNumber, d);
				}
				d.setPreVolData(ret);
			} else {
				VolData d = crh.getVolData(volumeNumber);
				if (d==null) {
					d = new VolData();
					crh.setVolData(volumeNumber, d);
				}
				d.setPostVolData(ret);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	private int getVolumeMaxSize(int volumeNumber) {
		for (VolumeTemplate t : volumeTemplates) {
			if (t==null) {
				System.out.println("VOLDATA NULL");
			}
			if (t.appliesTo(volumeNumber, crh.getVolumeCount())) {
				return t.getVolumeMaxSize();
			}
		}
		return 50;
	}
	
	private void trimEnd(StringBuilder sb, Page p) {
		int i = 0;
		int x = sb.length()-1;
		while (i<p.keepPreviousSheets() && x>0) {
			if (sb.charAt(x)=='s') {
				x--;
				i++;
			}
			if (sb.charAt(x)==ZERO_WIDTH_SPACE) {
				sb.deleteCharAt(x);
				x--;
			}
		}
	}

	public Iterable<Volume> getVolumes() {
		try {
			reformat();
		} catch (FormatterException e) {
			throw new RuntimeException("Error while reformatting.");
		}
		int j = 1;
		boolean ok = false;
		int totalPreCount = 0;
		int totalPostCount = 0;
		int prvVolCount = 0;
		int volumeOffset = 0;
		ArrayList<Volume> ret = new ArrayList<Volume>();
		while (!ok) {
			// make a preliminary calculation based on contents only
			Iterable<PageSequence> ps = crh.getContents().getContents();
			final int contents = PageTools.countSheets(ps); 
			ArrayList<Page> pages = new ArrayList<Page>();
			StringBuilder res = new StringBuilder();
			{
				boolean volBreakAllowed = true;
				for (PageSequence seq :ps) {
					StringBuilder sb = new StringBuilder();
					LayoutMaster lm = seq.getLayoutMaster();
					int pageIndex=0;
					for (Page p : seq) {
						if (!lm.duplex() || pageIndex%2==0) {
							volBreakAllowed = true;
							sb.append("s");
						}
						volBreakAllowed &= p.allowsVolumeBreak();
						trimEnd(sb, p);
						if (!lm.duplex() || pageIndex%2==1) {
							if (volBreakAllowed) {
								sb.append(ZERO_WIDTH_SPACE);
							}
						}
						pages.add(p);
						pageIndex++;
					}
					res.append(sb);
					res.append(ZERO_WIDTH_SPACE);
				}
			}
			logger.fine("Volume break string: " + res.toString().replace(ZERO_WIDTH_SPACE, '-'));
			BreakPointHandler volBreaks = new BreakPointHandler(res.toString());
			int splitterMax = getVolumeMaxSize(1);
						
			crh.setSDC(new EvenSizeVolumeSplitterCalculator(contents+totalPreCount+totalPostCount, splitterMax, volumeOffset));
			if (crh.getVolumeCount()!=prvVolCount) {
				prvVolCount = crh.getVolumeCount();
			}
			//System.out.println("volcount "+volumeCount() + " sheets " + sheets);
			boolean ok2 = true;
			totalPreCount = 0;
			totalPostCount = 0;
			ret = new ArrayList<Volume>();
			int pageIndex = 0;
			ArrayList<Iterable<PageSequence>> preV = new ArrayList<Iterable<PageSequence>>();
			ArrayList<Iterable<PageSequence>> postV = new ArrayList<Iterable<PageSequence>>();
			
			for (int i=1;i<=crh.getVolumeCount();i++) {
				if (splitterMax!=getVolumeMaxSize(i)) {
					logger.warning("Implementation does not support different target volume size. All volumes must have the same target size.");
				}
				preV.add(getPreVolumeContents(i).getContents());
				postV.add(getPostVolumeContents(i).getContents());
			}
			for (int i=1;i<=crh.getVolumeCount();i++) {
				
				totalPreCount += crh.getVolData(i).getPreVolSize();
				totalPostCount += crh.getVolData(i).getPostVolSize();

				int targetSheetsInVolume = crh.sheetsInVolume(i);
				if (i==crh.getVolumeCount()) {
					targetSheetsInVolume = splitterMax;
				}
				int contentSheets = targetSheetsInVolume-crh.getVolData(i).getVolOverhead();
				int offset = -1;
				BreakPoint bp;
				do {
					offset++;
					bp = volBreaks.tryNextRow(contentSheets+offset);
				} while (bp.getHead().length()<contentSheets && targetSheetsInVolume+offset<=splitterMax);
				bp = volBreaks.nextRow(contentSheets + offset, true);
				contentSheets = bp.getHead().length();
				crh.getVolData(i).setTargetVolSize(contentSheets + crh.getVolData(i).getVolOverhead());
			}
			for (int i=1;i<=crh.getVolumeCount();i++) {
				int contentSheets = crh.getVolData(i).getTargetVolSize() - crh.getVolData(i).getVolOverhead();
				logger.fine("Sheets  in volume " + i + ": " + (contentSheets+crh.getVolData(i).getVolOverhead()));
				PageStructCopy body = new PageStructCopy();
				while (true) {
					if (pageIndex>=pages.size()) {
						break;
					}
					if (body.countSheets(pages.get(pageIndex))<=contentSheets) {
						body.addPage(pages.get(pageIndex));
						pageIndex++;
					} else {
						break;
					}
				}
				int sheetsInVolume = PageTools.countSheets(body) + crh.getVolData(i).getVolOverhead();
				if (sheetsInVolume>crh.getVolData(i).getTargetVolSize()) {
					ok2 = false;
					logger.fine("Error in code. Too many sheets in volume " + i + ": " + sheetsInVolume);
				}
				ret.add(new VolumeImpl(preV.get(i-1), body, postV.get(i-1)));
			}
			if (volBreaks.hasNext()) {
				ok2 = false;
				logger.fine("There is more content... sheets: " + volBreaks.getRemaining() + ", pages: " +(pages.size()-pageIndex));
				if (volumeOffset<1) {
					volumeOffset++;
				} else {
					logger.warning("Could not fit contents even when adding a new volume.");
				}
			}
			if (!crh.isDirty() && pageIndex==pages.size() && ok2) {
				//everything fits
				ok = true;
			} else if (j>9) {
				throw new RuntimeException("Failed to complete volume division.");
			} else {
				j++;
				crh.setDirty(false);
				try {
					reformat();
				} catch (FormatterException e) {
					throw new RuntimeException("Error while reformatting.");
				}
				logger.info("Things didn't add up, running another iteration (" + j + ")");
			}
		}
		return ret;
	}
/*	
	class VolumeStructData implements Iterable<Volume> {
		private final List<Volume> ret;
		VolumeStructData(List<Volume> ret) {
			this.ret = ret;
		}
		public Iterator<Volume> iterator() {
			return ret.iterator();
		}
	};
*/
}
