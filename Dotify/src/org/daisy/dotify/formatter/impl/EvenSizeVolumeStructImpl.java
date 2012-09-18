package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.daisy.dotify.formatter.dom.BookStruct;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.Volume;
import org.daisy.dotify.formatter.dom.VolumeStruct;
import org.daisy.dotify.formatter.utils.PageTools;
import org.daisy.dotify.text.BreakPoint;
import org.daisy.dotify.text.BreakPointHandler;

class EvenSizeVolumeStructImpl implements VolumeStruct {
	private final Logger logger;

	// the struct
	private final BookStruct struct;
	
	private Integer[] volumeOverhead;
	private Integer[] targetVolSize;
	
	private final int splitterMax;
	private EvenSizeVolumeSplitterCalculator sdc;
	private boolean volumeForContentSheetChanged = false;
	private HashMap<Integer, Integer> volSheet;

	/**
	 * @param sheets, total number of sheets
	 * @param splitterMax, maximum number of sheets in a volume
	 */
	public EvenSizeVolumeStructImpl(BookStruct struct, int splitterMax) {
		this.struct = struct;
		this.splitterMax = splitterMax;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.volumeOverhead = new Integer[0];
		this.targetVolSize = new Integer[0];
		this.volSheet = new HashMap<Integer, Integer>();
		this.volumeForContentSheetChanged = false;
		struct.setVolumeStruct(this);
	}
	
	/*
	public boolean isBreakpoint(int sheetIndex) {
		return sdc.isBreakpoint(sheetIndex);
	}
*/
	public int getVolumeCount() {
		return sdc.getVolumeCount();
	}
/*
	public int getVolumeForSheet(int sheetIndex) {
		return sdc.getVolumeForSheet(sheetIndex);
	}
	*/
	public int getVolumeForContentSheet(int sheetIndex) {
		if (sheetIndex<1) {
			throw new IndexOutOfBoundsException("Sheet index must be greater than zero: " + sheetIndex);
		}
		if (sheetIndex>sdc.getSheetCount()) {
			throw new IndexOutOfBoundsException("Sheet index must not exceed agreed value.");
		}
		int lastSheetInCurrentVolume=0;
		int retVolume=0;
		while (lastSheetInCurrentVolume<sheetIndex) {
			if (retVolume<volumeOverhead.length && volumeOverhead[retVolume]!=null) {
				lastSheetInCurrentVolume -= volumeOverhead[retVolume];
			}
			if (retVolume<targetVolSize.length && targetVolSize[retVolume]!=null) {
				lastSheetInCurrentVolume += targetVolSize[retVolume];
			} else {
				lastSheetInCurrentVolume += sdc.sheetsInVolume(retVolume+1);
			}
			retVolume++;
		}
		Integer cv = volSheet.get(sheetIndex);
		if (cv==null || cv!=retVolume) {
			volumeForContentSheetChanged = true;
			volSheet.put(sheetIndex, retVolume);
		}
		return retVolume;
	}
	
	public Iterator<Volume> iterator() {
		int j = 1;
		boolean ok = false;
		int totalPreCount = 0;
		int totalPostCount = 0;
		int prvVolCount = 0;
		
		ArrayList<Volume> ret = new ArrayList<Volume>();
		while (!ok) {
			// make a preliminary calculation based on contents only
			Iterable<PageSequence> ps = struct.getContentsPageStruct().getContents();
			final int contents = PageTools.countSheets(ps); 
			ArrayList<Page> pages = new ArrayList<Page>();
			StringBuilder sb = new StringBuilder();
			{
				boolean volBreakAllowed = true;
				for (PageSequence seq :ps) {
					LayoutMaster lm = seq.getLayoutMaster();
					int pageIndex=0;
					for (Page p : seq) {
						if (!lm.duplex() || pageIndex%2==0) {
							volBreakAllowed = true;
							sb.append("s");
						}
						volBreakAllowed &= p.allowsVolumeBreak();
						if (!lm.duplex() || pageIndex%2==1) {
							if (volBreakAllowed) {
								sb.append("\u200b");
							}
						}
						pages.add(p);
						pageIndex++;
					}
				}
			}
			logger.fine("Volume break string: " + sb.toString().replace('\u200b', '-'));
			BreakPointHandler volBreaks = new BreakPointHandler(sb.toString());

			volumeForContentSheetChanged = false;
			sdc = new EvenSizeVolumeSplitterCalculator(contents+totalPreCount+totalPostCount, splitterMax);
			if (sdc.getVolumeCount()!=prvVolCount) {
				volumeOverhead = new Integer[sdc.getVolumeCount()];
				targetVolSize = new Integer[sdc.getVolumeCount()];
				prvVolCount = sdc.getVolumeCount();
			}
			//System.out.println("volcount "+volumeCount() + " sheets " + sheets);
			boolean ok2 = true;
			totalPreCount = 0;
			totalPostCount = 0;
			ret = new ArrayList<Volume>();
			int pageIndex = 0;
			ArrayList<Iterable<PageSequence>> preV = new ArrayList<Iterable<PageSequence>>();
			ArrayList<Iterable<PageSequence>> postV = new ArrayList<Iterable<PageSequence>>();
			for (int i=1;i<=getVolumeCount();i++) {
				preV.add(struct.getPreVolumeContents(i).getContents());
				postV.add(struct.getPostVolumeContents(i).getContents());
			}
			for (int i=1;i<=getVolumeCount();i++) {
				int preCount = PageTools.countSheets(preV.get(i-1));
				int postCount = PageTools.countSheets(postV.get(i-1));
				totalPreCount += preCount;
				totalPostCount += postCount;
				if ((i-1)<volumeOverhead.length) {
					volumeOverhead[i-1] = preCount + postCount;
					int targetSheetsInVolume = sdc.sheetsInVolume(i);
					if (i==getVolumeCount()) {
						targetSheetsInVolume = splitterMax;
					}
					int contentSheets = targetSheetsInVolume-volumeOverhead[i-1];
					int offset = -1;
					BreakPoint bp;
					do {
						offset++;
						bp = volBreaks.tryNextRow(contentSheets+offset);
					} while (bp.getHead().length()<contentSheets && targetSheetsInVolume+offset<=splitterMax);
					bp = volBreaks.nextRow(contentSheets + offset, true);
					contentSheets = bp.getHead().length();
					targetVolSize[i-1] = contentSheets + volumeOverhead[i-1];
				} else {
					throw new RuntimeException("Error in code: " + i);
				}
			}
			for (int i=1;i<=getVolumeCount();i++) {
				int contentSheets = targetVolSize[i-1] - volumeOverhead[i-1];
				logger.fine("Sheets  in volume " + i + ": " + (contentSheets+volumeOverhead[i-1]));
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
				int sheetsInVolume = PageTools.countSheets(body) + volumeOverhead[i-1];
				if (sheetsInVolume>targetVolSize[i-1]) {
					ok2 = false;
					logger.fine("Error in code. Too many sheets in volume " + i + ": " + sheetsInVolume);
				}
				ret.add(new VolumeImpl(preV.get(i-1), body, postV.get(i-1)));
			}
			if (volBreaks.hasNext()) {
				ok2 = false;
				logger.fine("There is more content... sheets: " + volBreaks.getRemaining() + ", pages: " +(pages.size()-pageIndex));
			}
			if (!struct.isDirty() && pageIndex==pages.size() && ok2 && (!volumeForContentSheetChanged)) {
				//everything fits
				ok = true;
			} else if (j>9) {
				throw new RuntimeException("Failed to complete volume division.");
			} else {
				j++;
				struct.resetDirty();
				logger.info("Things didn't add up, running another iteration (" + j + ")");
			}
		}
		return ret.iterator();
	}

}