package org.daisy.dotify.book;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.formatter.CrossReferences;
import org.daisy.dotify.formatter.LayoutMaster;

class CrossReferenceHandler implements CrossReferences {
	private final Map<String, Integer> volLocations;
	private final Map<String, Integer> pageLocations;
	private final Map<Integer, VolData> volData;

	private HashMap<Integer, Integer> volSheet;
	private Map<Page, Integer> pageSheetMap;
	private PageStruct ps;
	private EvenSizeVolumeSplitterCalculator sdc;

	private Integer[] targetVolSize;
	
	private boolean isDirty;
	private boolean volumeForContentSheetChanged;
	private int maxKey;
	
	public CrossReferenceHandler() {
		this.volLocations = new HashMap<String, Integer>();
		this.pageLocations = new HashMap<String, Integer>();

		this.volData = new HashMap<Integer, VolData>();
		this.volSheet = new HashMap<Integer, Integer>();
		this.targetVolSize = new Integer[0];
		this.isDirty = false;
		this.volumeForContentSheetChanged = false;
		this.maxKey = 0;
	}
	
	public PageStruct getContents() {
		return ps;
	}
	
	public void setContents(PageStruct contents) {
		this.ps = contents;
		this.sdc = new EvenSizeVolumeSplitterCalculator(PageTools.countSheets(ps.getContents()), 50);
		int sheetIndex=0;
		this.pageSheetMap = new HashMap<Page, Integer>();
		for (PageSequence s : ps.getContents()) {
			LayoutMaster lm = s.getLayoutMaster();
			int pageIndex=0;
			for (Page p : s.getPages()) {
				if (!lm.duplex() || pageIndex%2==0) {
					sheetIndex++;
				}
				pageSheetMap.put(p, sheetIndex);
				pageIndex++;
			}
		}
	}
	
	public void setSDC(EvenSizeVolumeSplitterCalculator sdc) {
		volumeForContentSheetChanged = false;
		this.sdc = sdc;
	}
	
	public int sheetsInVolume(int volIndex) {
		return sdc.sheetsInVolume(volIndex);
	}
	
	public void setVolData(int volumeNumber, VolData d) {
		//update the highest observed volume number
		maxKey = Math.max(maxKey, volumeNumber);
		volData.put(volumeNumber, d);
	}
	
	public VolData getVolData(int volumeNumber) {
		if (volData.get(volumeNumber)==null) {
			setVolData(volumeNumber, new VolData());
		}
		return volData.get(volumeNumber);
	}
	
	public boolean isDirty() {
		return isDirty || volumeForContentSheetChanged;
	}

	public void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	public int updateVolumeLocation(String refid, int vol) {
		Integer v = volLocations.get(refid);
		volLocations.put(refid, vol);
		if (v!=null && v!=vol) {
			//this refid has been requested before and it changed location
			isDirty = true;
		}
		return vol;
	}
	
	public Page updatePageLocation(String refid, Page page) {
		Integer p = pageLocations.get(refid);
		pageLocations.put(refid, page.getPageIndex());
		if (p!=null && p!=page.getPageIndex()) {
			//this refid has been requested before and it changed location
			isDirty = true;
		}
		return page;
	}
	
	public Integer getVolumeNumber(String refid) {
		for (int i=1; i<=sdc.getVolumeCount(); i++) {
			if (volData.get(i)!=null) {
				if (volData.get(i).getPreVolData()!=null && volData.get(i).getPreVolData().getPage(refid)!=null) {
					return updateVolumeLocation(refid, i);
				}
				if (volData.get(i).getPostVolData()!=null &&  volData.get(i).getPostVolData().getPage(refid)!=null) {
					return updateVolumeLocation(refid, i);
				}
			}
		}
		Integer i = pageSheetMap.get(getPage(refid));
		if (i!=null) {
			return updateVolumeLocation(refid, getVolumeForContentSheet(i));
		}
		setDirty(true);
		return null;
	}

	public Page getPage(String refid) {
		Page ret;
		if (ps!=null && (ret=ps.getPage(refid))!=null) {
			return updatePageLocation(refid, ret);
		}
		if (sdc!=null) {
			for (int i=1; i<=sdc.getVolumeCount(); i++) {
				if (volData.get(i)!=null) {
					if (volData.get(i).getPreVolData()!=null && (ret=volData.get(i).getPreVolData().getPage(refid))!=null) {
						return updatePageLocation(refid, ret);
					}
					if (volData.get(i).getPostVolData()!=null &&  (ret=volData.get(i).getPostVolData().getPage(refid))!=null) {
						return updatePageLocation(refid, ret);
					}
				}
			}
		}
		setDirty(true);
		return null;
	}
	
	
	private int getVolumeForContentSheet(int sheetIndex) {
		if (sheetIndex<1) {
			throw new IndexOutOfBoundsException("Sheet index must be greater than zero: " + sheetIndex);
		}
		if (sheetIndex>sdc.getSheetCount()) {
			throw new IndexOutOfBoundsException("Sheet index must not exceed agreed value.");
		}
		int lastSheetInCurrentVolume=0;
		int retVolume=0;
		while (lastSheetInCurrentVolume<sheetIndex) {
			lastSheetInCurrentVolume -= getVolData(retVolume).getVolOverhead();
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
	
	public int getExpectedVolumeCount() {
		return sdc.getVolumeCount();
	}

}
