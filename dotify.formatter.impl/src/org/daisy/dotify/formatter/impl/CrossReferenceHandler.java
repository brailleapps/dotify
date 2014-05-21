package org.daisy.dotify.formatter.impl;

import java.util.HashMap;
import java.util.Map;

import org.daisy.dotify.api.formatter.Context;

class CrossReferenceHandler implements CrossReferences, Context {
	private final Map<String, Integer> volLocations;
	private final Map<String, Integer> pageLocations;
	private final Map<Integer, VolData> volData;

	private HashMap<Integer, Integer> volSheet;
	private Map<Page, Integer> pageSheetMap;
	private PageStructBuilder ps;
	private EvenSizeVolumeSplitterCalculator sdc;
	
	private boolean isDirty;
	private boolean volumeForContentSheetChanged;
	
	private Integer currentVolume, currentPage, volumeCount, contextVolume, contextPage;
	private boolean metaContext = false;
	//private int maxKey;
	
	public CrossReferenceHandler() {
		this.volLocations = new HashMap<String, Integer>();
		this.pageLocations = new HashMap<String, Integer>();

		this.volData = new HashMap<Integer, VolData>();
		this.volSheet = new HashMap<Integer, Integer>();
		this.isDirty = false;
		this.volumeForContentSheetChanged = false;
		//this.maxKey = 0;
	}
	
	public PageStructBuilder getContents() {
		return ps;
	}
	
	public void setContents(PageStructBuilder contents, int splitterMax) {
		this.ps = contents;
		this.sdc = new EvenSizeVolumeSplitterCalculator(PageTools.countSheets(ps.getContents()), splitterMax);
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
	
	public EvenSizeVolumeSplitterCalculator getSdc() {
		return sdc;
	}

	public void setSDC(EvenSizeVolumeSplitterCalculator sdc) {
		volumeForContentSheetChanged = false;
		this.sdc = sdc;
	}
	
	public int sheetsInVolume(int volIndex) {
		return sdc.sheetsInVolume(volIndex);
	}
	
	private void setVolData(int volumeNumber, VolData d) {
		//update the highest observed volume number
		//maxKey = Math.max(maxKey, volumeNumber);
		volData.put(volumeNumber, d);
	}
	
	public void setPreVolData(int volumeNumber, PageStructBuilder preVolData) {
		VolData d = (VolData)getVolData(volumeNumber);
		/*if (d.preVolData!=preVolData) {
			setDirty(true);
		}*/
		d.setPreVolData(preVolData);
	}
	
	public void setPostVolData(int volumeNumber, PageStructBuilder postVolData) {
		VolData d = (VolData)getVolData(volumeNumber);
		/*if (d.postVolData!=postVolData) {
			setDirty(true);
		}*/
		d.setPostVolData(postVolData);
	}
	
	public void setTargetVolSize(int volumeNumber, int targetVolSize) {
		VolData d = (VolData)getVolData(volumeNumber);
		if (d.getTargetVolSize()!=targetVolSize) {
			setDirty(true);
		}
		d.setTargetVolSize(targetVolSize);
	}
	
	public VolDataInterface getVolData(int volumeNumber) {
		if (volumeNumber<1) {
			throw new IndexOutOfBoundsException("Volume must be greater than or equal to 1");
		}
		if (volData.get(volumeNumber)==null) {
			setVolData(volumeNumber, new VolData());
			setDirty(true);
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
	
	public PageImpl updatePageLocation(String refid, PageImpl page) {
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

	private PageImpl getPage(String refid) {
		PageImpl ret;
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
	
	public Integer getPageNumber(String refid) {
		PageImpl p = getPage(refid);
		if (p==null) {
			return null;
		} else {
			return p.getPageIndex()+1;
		}
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
		do {
			retVolume++;
			int prvVal = lastSheetInCurrentVolume;
			int volSize = getVolData(retVolume).getTargetVolSize();
			if (volSize==0) {
				volSize = sdc.sheetsInVolume(retVolume);
			}
			lastSheetInCurrentVolume += volSize;
			lastSheetInCurrentVolume -= getVolData(retVolume).getVolOverhead();
			if (prvVal>=lastSheetInCurrentVolume) {
				throw new RuntimeException("Negative volume size");
			}
		} while (sheetIndex>lastSheetInCurrentVolume);
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
	
	private class VolData implements VolDataInterface {
		private PageStructBuilder preVolData;
		private PageStructBuilder postVolData;
		private int preVolSize;
		private int postVolSize;
		private int targetVolSize;
		
		private VolData() {
			this.preVolSize = 0;
			this.postVolSize = 0;
			this.targetVolSize = 0;
		}

		public PageStructBuilder getPreVolData() {
			return preVolData;
		}

		public void setPreVolData(PageStructBuilder preVolData) {
			//use the highest value to avoid oscillation
			preVolSize = Math.max(preVolSize, PageTools.countSheets(preVolData.getContents()));
			this.preVolData = preVolData;
		}

		public PageStructBuilder getPostVolData() {
			return postVolData;
		}

		public void setPostVolData(PageStructBuilder postVolData) {
			//use the highest value to avoid oscillation
			postVolSize = Math.max(postVolSize, PageTools.countSheets(postVolData.getContents()));
			this.postVolData = postVolData;
		}

		public int getPreVolSize() {
			return preVolSize;
		}

		public int getPostVolSize() {
			return postVolSize;
		}
		
		public int getVolOverhead() {
			return preVolSize + postVolSize;
		}

		public int getTargetVolSize() {
			return targetVolSize;
		}

		public void setTargetVolSize(int targetVolSize) {
			this.targetVolSize = targetVolSize;
		}
	}

	public Integer getCurrentVolume() {
		if (currentVolume==null) {
			setDirty(true);
		}
		return currentVolume;
	}

	public void setCurrentVolume(Integer currentVolume) {
		this.currentVolume = currentVolume;
	}

	public Integer getCurrentPage() {
		if (currentPage==null) {
			setDirty(true);
		}
		return currentPage;
	}

	public void setCurrentPage(Integer currentPage) {
		this.currentPage = currentPage;
	}

	public Integer getVolumeCount() {
		if (volumeCount==null) {
			setDirty(true);
		}
		return volumeCount;
	}

	public void setVolumeCount(Integer volumeCount) {
		this.volumeCount = volumeCount;
	}
	
	/**
	 * Set's the meta context to true.
	 * @param value
	 */
	public void setMetaContext(boolean value) {
		metaContext = value;
	}

	public Integer getMetaVolume() {
		if (metaContext && contextVolume==null) {
			setDirty(true);
		}
		return contextVolume;
	}

	public void setMetaVolume(Integer contextVolume) {
		this.contextVolume = contextVolume;
	}

	public Integer getMetaPage() {
		if (metaContext && contextPage==null) {
			setDirty(true);
		}
		return contextPage;
	}
	
	public void setMetaPage(Integer contextPage) {
		this.contextPage = contextPage;
	}


}
