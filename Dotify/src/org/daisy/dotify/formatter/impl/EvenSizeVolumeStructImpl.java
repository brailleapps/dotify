package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Logger;

import org.daisy.dotify.formatter.PageTools;
import org.daisy.dotify.formatter.dom.BookStruct;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.Volume;
import org.daisy.dotify.formatter.dom.VolumeStruct;

class EvenSizeVolumeStructImpl implements VolumeStruct {
	private final Logger logger;

	// the struct
	private final BookStruct struct;
	
	private Integer[] volumeOverhead;
	
	private final int splitterMax;
	private EvenSizeVolumeSplitterCalculator sdc;

	/**
	 * @param sheets, total number of sheets
	 * @param splitterMax, maximum number of sheets in a volume
	 */
	public EvenSizeVolumeStructImpl(BookStruct struct, int splitterMax) {
		this.struct = struct;
		this.splitterMax = splitterMax;
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.volumeOverhead = new Integer[0];
	}
	
	private void calc(int sheets) {
		this.sdc = new EvenSizeVolumeSplitterCalculator(sheets, splitterMax);
		this.volumeOverhead = new Integer[sdc.getVolumeCount()];
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
		int i=0;
		int j=0;
		while (i<sheetIndex) {
			if (j<volumeOverhead.length && volumeOverhead[j]!=null) {
				i -= volumeOverhead[j];
			}
			i += sheetsInVolume(j);
			j++;
		}
		return j;
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
		ArrayList<Volume> ret = new ArrayList<Volume>();
		while (!ok) {
			calc(contents+totalPreCount+totalPostCount);
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
			if (pageIndex==pages.size() && ok2) {
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

	public int sheetsInVolume(int volIndex) {
		return sdc.sheetsInVolume(volIndex);
	}
}