package org.daisy.dotify.book;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.daisy.dotify.formatter.BlockSequence;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.LayoutMaster;
import org.daisy.dotify.paginator.Page;
import org.daisy.dotify.paginator.PageSequence;
import org.daisy.dotify.paginator.PageStruct;
import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorException;
import org.daisy.dotify.paginator.PaginatorFactory;
import org.daisy.dotify.text.BreakPoint;
import org.daisy.dotify.text.BreakPointHandler;
import org.daisy.dotify.tools.CompoundIterable;

/**
 * Provides a default implementation of BookStruct
 * 
 * @author Joel Håkansson
 */
public class BookStruct {
	private final static char ZERO_WIDTH_SPACE = '\u200b';
	private final Logger logger;
	private final Paginator contentPaginator;
	
	private final VolumeContentFormatter volumeFormatter;
	private final FormatterFactory formatterFactory;
	private final PaginatorFactory paginatorFactory;

	private final CrossReferenceHandler crh;

	public BookStruct(Paginator content, VolumeContentFormatter volumeFormatter,
			FormatterFactory factory, PaginatorFactory paginatorFactory) {
		this.contentPaginator = content;
		this.formatterFactory = factory;
		this.paginatorFactory = paginatorFactory;
		this.volumeFormatter = volumeFormatter;
		
		this.logger = Logger.getLogger(BookStruct.class.getCanonicalName());

		this.crh = new CrossReferenceHandler();
	}
	
	private void reformat(int splitterMax) throws PaginatorException {
		crh.setContents(contentPaginator.paginate(crh), splitterMax);
		//paginator.close();
	}

	private PageStruct getPreVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, true);
	}

	private PageStruct getPostVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, false);
	}

	private PageStruct getVolumeContents(int volumeNumber, boolean pre) {
		try {
			List<Iterable<BlockSequence>> ib;
			if (pre) {
				ib = volumeFormatter.formatPreVolumeContents(volumeNumber, crh.getExpectedVolumeCount(), crh);
			} else {
				ib = volumeFormatter.formatPostVolumeContents(volumeNumber, crh.getExpectedVolumeCount(), crh);
			}
			Paginator paginator2 = paginatorFactory.newPaginator();
			paginator2.open(formatterFactory, new CompoundIterable<BlockSequence>(ib));
			PageStruct ret = paginator2.paginate(crh);
			paginator2.close();
			if (pre) {
				crh.setPreVolData(volumeNumber, ret);
			} else {
				crh.setPostVolData(volumeNumber, ret);
			}
			return ret;
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (PaginatorException e) {
			throw new RuntimeException(e);
		}
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

	@SuppressWarnings("unchecked")
	public Iterable<Volume> getVolumes() {
		try {
			reformat(50);
		} catch (PaginatorException e) {
			throw new RuntimeException("Error while reformatting.");
		}
		int j = 1;
		boolean ok = false;
		int totalPreCount = 0;
		int totalPostCount = 0;
		int prvVolCount = 0;
		int volumeOffset = 0;
		int volsMin = Integer.MAX_VALUE;
		ArrayList<Volume> ret = new ArrayList<Volume>();
		while (!ok) {
			// make a preliminary calculation based on contents only
			Iterable<? extends PageSequence> ps = crh.getContents().getContents();
			final int contents = PageTools.countSheets(ps); 
			ArrayList<Page> pages = new ArrayList<Page>();
			StringBuilder res = new StringBuilder();
			{
				boolean volBreakAllowed = true;
				for (PageSequence seq :ps) {
					StringBuilder sb = new StringBuilder();
					LayoutMaster lm = seq.getLayoutMaster();
					int pageIndex=0;
					for (Page p : seq.getPages()) {
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
			int splitterMax = volumeFormatter.getVolumeMaxSize(1, crh.getExpectedVolumeCount());

			EvenSizeVolumeSplitterCalculator esc;
			esc = new EvenSizeVolumeSplitterCalculator(contents + totalPreCount + totalPostCount, splitterMax, volumeOffset);
			// this fixes a problem where the volume overhead pushes the
			// volume count up once the volume offset has been set
			if (volumeOffset == 1 && esc.getVolumeCount() > volsMin + 1) {
				volumeOffset = 0;
				esc = new EvenSizeVolumeSplitterCalculator(contents + totalPreCount + totalPostCount, splitterMax, volumeOffset);
			}

			volsMin = Math.min(esc.getVolumeCount(), volsMin);
			
			crh.setSDC(esc);

			if (crh.getExpectedVolumeCount()!=prvVolCount) {
				prvVolCount = crh.getExpectedVolumeCount();
			}
			//System.out.println("volcount "+volumeCount() + " sheets " + sheets);
			boolean ok2 = true;
			totalPreCount = 0;
			totalPostCount = 0;
			ret = new ArrayList<Volume>();
			int pageIndex = 0;
			ArrayList<Iterable<PageSequence>> preV = new ArrayList<Iterable<PageSequence>>();
			ArrayList<Iterable<PageSequence>> postV = new ArrayList<Iterable<PageSequence>>();
			
			for (int i=1;i<=crh.getExpectedVolumeCount();i++) {
				if (splitterMax!=volumeFormatter.getVolumeMaxSize(i, crh.getExpectedVolumeCount())) {
					logger.warning("Implementation does not support different target volume size. All volumes must have the same target size.");
				}
				preV.add((Iterable<PageSequence>) getPreVolumeContents(i).getContents());
				postV.add((Iterable<PageSequence>) getPostVolumeContents(i).getContents());
			}
			for (int i=1;i<=crh.getExpectedVolumeCount();i++) {
				
				totalPreCount += crh.getVolData(i).getPreVolSize();
				totalPostCount += crh.getVolData(i).getPostVolSize();

				int targetSheetsInVolume = crh.sheetsInVolume(i);
				if (i==crh.getExpectedVolumeCount()) {
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
				crh.setTargetVolSize(i, contentSheets + crh.getVolData(i).getVolOverhead());
			}
			for (int i=1;i<=crh.getExpectedVolumeCount();i++) {
				int contentSheets = crh.getVolData(i).getTargetVolSize() - crh.getVolData(i).getVolOverhead();
				logger.fine("Sheets  in volume " + i + ": " + (contentSheets+crh.getVolData(i).getVolOverhead()) + 
						", content:" + contentSheets +
						", overhead:" + crh.getVolData(i).getVolOverhead());
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
				if (!crh.isDirty()) {
					if (volumeOffset < 1) {
						//First check to see if the page increase can will be handled automatically without increasing volume offset 
						//in the next iteration (by supplying up-to-date overhead values)
						EvenSizeVolumeSplitterCalculator esv = new EvenSizeVolumeSplitterCalculator(contents+totalPreCount+totalPostCount, splitterMax, volumeOffset);
						if (esv.equals(crh.getSdc())) {
							volumeOffset++;
						}
					} else {
						logger.warning("Could not fit contents even when adding a new volume.");
					}
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
					reformat(volumeFormatter.getVolumeMaxSize(1, crh.getExpectedVolumeCount()));
				} catch (PaginatorException e) {
					throw new RuntimeException("Error while reformatting.", e);
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
