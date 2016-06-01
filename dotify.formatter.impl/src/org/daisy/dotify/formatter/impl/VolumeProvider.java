package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.common.layout.SplitPoint;
import org.daisy.dotify.common.layout.SplitPointHandler;

/**
 * Provides contents for a volume 
 * @author Joel Håkansson
 *
 */
public class VolumeProvider {
	private List<Sheet> units;
	private final SplitPointHandler<Sheet> volSplitter = new SplitPointHandler<>();
	private final  CrossReferenceHandler crh;
	private final PageStructBuilder contentPaginator;
	//PageStruct ps;
	private int pageIndex = 0;
	//private int totalPageCount = 0;
	private int i=0;

	public VolumeProvider(PageStructBuilder contentPaginator, CrossReferenceHandler crh, DefaultContext rcontext) {
		this.crh = crh;
		this.contentPaginator = contentPaginator;
		try {
			units = contentPaginator.paginate(rcontext);
		} catch (PaginatorException e) {
			throw new RuntimeException("Error while reformatting.", e);
		}
	}
	
	List<Sheet> nextVolume(int targetSheetsInVolume, int overhead, int splitterMax, ArrayList<AnchorData> ad) {
		i++;
		SplitPoint<Sheet> sp = getSplitPoint(targetSheetsInVolume, overhead, splitterMax);
		units = sp.getTail();
		List<Sheet> contents = sp.getHead();
		int pageCount = FormatterImpl.countPages(contents);
		// TODO: In a volume-by-volume scenario, how can we make this work
		contentPaginator.setVolumeScope(i, pageIndex, pageIndex+pageCount); 
		pageIndex += pageCount;
		//totalPageCount += pageCount;
		for (Sheet sheet : contents) {
			for (PageImpl p : sheet.getPages()) {
				for (String id : p.getIdentifiers()) {
					crh.setVolumeNumber(id, i);
				}
				if (p.getAnchors().size()>0) {
					ad.add(new AnchorData(p.getPageIndex(), p.getAnchors()));
				}
			}
		}
		return contents;
	}
	
	/**
	 * The total number of pages provided so far
	 * @return the number of pages
	 */
	int getTotalPageCount() {
		//return totalPageCount;
		return pageIndex;
	}
	
	/**
	 * Gets the current page index.
	 * @return returns the page index
	 */
	int getPageIndex() {
		return pageIndex;
	}
	
	private SplitPoint<Sheet> getSplitPoint(int targetSheetsInVolume, int overhead, int splitterMax) {
		int contentSheets = targetSheetsInVolume-overhead;
		SplitPoint<Sheet> bp;
		{
			int offset = -1;
			do {
				offset++;
				bp = volSplitter.split(contentSheets+offset, false, units);
			} while (bp.getHead().size()<contentSheets && targetSheetsInVolume+offset<splitterMax);
			bp = volSplitter.split(contentSheets + offset, true, units);
		}
		return bp;
	}
	
	boolean hasNext() {
		return !units.isEmpty();
	}
	
	List<Sheet> getRemaining() {
		return units;
	}


}
