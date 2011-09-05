package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.logging.Logger;

import org.daisy.dotify.formatter.dom.BookStruct;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.Volume;
import org.daisy.dotify.formatter.dom.VolumeStruct;
import org.daisy.dotify.formatter.utils.PageTools;

class EvenSizeVolumeStructImpl implements VolumeStruct {
	private final Logger logger;

	// the struct
	private final BookStruct struct;
	
	private Integer[] volumeOverhead;
	
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
		this.volSheet = new HashMap<Integer, Integer>();
		this.volumeForContentSheetChanged = false;
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
			retVolume++;
			lastSheetInCurrentVolume += sheetsInVolume(retVolume);
		}
		Integer cv = volSheet.get(sheetIndex);
		if (cv==null || cv!=retVolume) {
			volumeForContentSheetChanged = true;
			volSheet.put(sheetIndex, retVolume);
		}
		return retVolume;
	}
	
	public Iterator<Volume> iterator() {
		// make a preliminary calculation based on contents only
		final int contents = PageTools.countSheets(struct.getPageStruct().getContents()); 
		ArrayList<Page> pages = new ArrayList<Page>();
		for (PageSequence seq : struct.getPageStruct().getContents()) {
			for (Page p : seq) {
				pages.add(p);
			}
		}
		int j = 1;
		boolean ok = false;
		int totalPreCount = 0;
		int totalPostCount = 0;
		int prvVolCount = 0;
		ArrayList<Volume> ret = new ArrayList<Volume>();
		while (!ok) {
			volumeForContentSheetChanged = false;
			sdc = new EvenSizeVolumeSplitterCalculator(contents+totalPreCount+totalPostCount, splitterMax);
			if (sdc.getVolumeCount()!=prvVolCount) {
				volumeOverhead = new Integer[sdc.getVolumeCount()];
				prvVolCount = sdc.getVolumeCount();
			}
			//System.out.println("volcount "+volumeCount() + " sheets " + sheets);
			boolean ok2 = true;
			totalPreCount = 0;
			totalPostCount = 0;
			ret = new ArrayList<Volume>();
			int pageIndex = 0;
			for (int i=1;i<=getVolumeCount();i++) {
				Iterable<PageSequence> pre = struct.getPreVolumeContents(i, this);
				Iterable<PageSequence> post = struct.getPostVolumeContents(i, this);
				int preCount = PageTools.countSheets(pre);
				int postCount = PageTools.countSheets(post);
				if ((i-1)<volumeOverhead.length) {
					volumeOverhead[i-1] = preCount + postCount;
				} else {
					throw new RuntimeException("Error in code: " + i);
				}
			}
			for (int i=1;i<=getVolumeCount();i++) {
				Iterable<PageSequence> pre = struct.getPreVolumeContents(i, this);
				Iterable<PageSequence> post = struct.getPostVolumeContents(i, this);
				int preCount = PageTools.countSheets(pre);
				int postCount = PageTools.countSheets(post);
				totalPreCount += preCount;
				totalPostCount += postCount;
				int contentSheets = sheetsInVolume(i)-preCount-postCount;
				//System.out.println("DEBUG " + preCount + " " + postCount + " " + contentSheets + " " + sheetsInVolume(i));
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
				int sheetsInVolume = preCount + PageTools.countSheets(body) + postCount;
				if (sheetsInVolume>sheetsInVolume(i)) {
					ok2 = false;
					//throw new RuntimeException("Error in code. Expected " + sheetsInVolume(i) + ", actual " + sheetsInVolume);
				}
				ret.add(new VolumeImpl(pre, body, post));
			}
			if (pageIndex==pages.size() && ok2 && (!volumeForContentSheetChanged)) {
				//everything fits
				ok = true;
			} else if (j>9) {
				throw new RuntimeException("Error in code.");
			} else {
				j++;
				logger.fine("Things didn't add up, running another iteration (" + j + ")");
			}
		}
		return ret.iterator();
	}

	/**
	 * 
	 * @param volIndex, volume index, one-based
	 * @return
	 */
	public int sheetsInVolume(int volIndex) {
		return sdc.sheetsInVolume(volIndex);
	}
}