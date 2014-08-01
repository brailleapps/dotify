package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.ContentCollection;
import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterCore;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.text.BreakPoint;
import org.daisy.dotify.text.BreakPointHandler;
import org.daisy.dotify.tools.StateObject;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson
 */
public class FormatterImpl implements Formatter, CrossReferences {
	private final static char ZERO_WIDTH_SPACE = '\u200b';
	private final static int DEFAULT_SPLITTER_MAX = 50;
	
	private final HashMap<String, TableOfContentsImpl> tocs;
	private final Stack<VolumeTemplate> volumeTemplates;
	private final Logger logger;
	
	private final StateObject state;
	private final FormatterContext context;
	private final Stack<BlockSequence> blocks;
	
	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslator translator) {
		this(new FormatterContext(translator));
	}
	
	public FormatterImpl(FormatterContext context) {
		this.context = context;
		this.blocks = new Stack<BlockSequence>();
		this.state = new StateObject();
		this.tocs = new HashMap<String, TableOfContentsImpl>();
		this.volumeTemplates = new Stack<VolumeTemplate>();
		
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		
		//CrossReferenceHandler
		this.volLocations = new HashMap<String, Integer>();
		this.pageLocations = new HashMap<String, Integer>();
		this.volumes = new HashMap<Integer, Volume>();
		this.volSheet = new HashMap<Integer, Integer>();
		this.isDirty = false;
		this.volumeForContentSheetChanged = false;
	}
	
	@Override
	public FormatterCore newSequence(SequenceProperties p) {
		state.assertOpen();
		BlockSequence currentSequence = new BlockSequence(p.getInitialPageNumber(), context.getMasters().get(p.getMasterName()));
		blocks.push(currentSequence);
		return currentSequence;
	}

	@Override
	public LayoutMasterBuilder newLayoutMaster(String name,
			LayoutMasterProperties properties) {
		return context.newLayoutMaster(name, properties);
	}

	@Override
	public void open() {
		state.assertUnopened();
		state.open();
	}
	
	@Override
	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}

	@Override
	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props) {
		VolumeTemplate template = new VolumeTemplate(tocs, props.getCondition(), props.getSplitterMax());
		volumeTemplates.push(template);
		return template;
	}

	@Override
	public TableOfContents newToc(String tocName) {
		TableOfContentsImpl toc = new TableOfContentsImpl();
		tocs.put(tocName, toc);
		return toc;
	}

	@Override
	public ContentCollection newCollection(String collectionId) {
		return context.newContentCollection(collectionId);
	}
	
	@Override
	public void write(PagedMediaWriter writer) {
		WriterHandler wh = new WriterHandler();
		wh.write(getVolumes(), writer);
		try {
			writer.close();
		} catch (IOException e) {
		}
	}

	private Iterable<Volume> getVolumes() {
		PageStructBuilder contentPaginator =  new PageStructBuilder(context, blocks);

		int j = 1;
		boolean ok = false;
		int totalOverheadCount = 0;
		int prvVolCount = 0;
		int volumeOffset = 0;
		int volsMin = Integer.MAX_VALUE;
		int reformatSplitterMax = DEFAULT_SPLITTER_MAX;
		ArrayList<Volume> ret = new ArrayList<Volume>();
		while (!ok) {
			try {
				this.ps = contentPaginator.paginate(this, new DefaultContext(null, null));
				this.sdc = new EvenSizeVolumeSplitterCalculator(ps.countSheets(), reformatSplitterMax);
			} catch (PaginatorException e) {
				throw new RuntimeException("Error while reformatting.", e);
			}
			// make a preliminary calculation based on contents only
			final int contents = ps.countSheets(); 
			String breakpoints = ps.buildBreakpointString();
			logger.fine("Volume break string: " + breakpoints.replace(ZERO_WIDTH_SPACE, '-'));
			BreakPointHandler volBreaks = new BreakPointHandler(breakpoints);
			int splitterMax = getVolumeMaxSize(1,  sdc.getVolumeCount());

			{
				EvenSizeVolumeSplitterCalculator esc = new EvenSizeVolumeSplitterCalculator(contents + totalOverheadCount, splitterMax, volumeOffset);
				// this fixes a problem where the volume overhead pushes the
				// volume count up once the volume offset has been set
				if (volumeOffset == 1 && esc.getVolumeCount() > volsMin + 1) {
					volumeOffset = 0;
					esc = new EvenSizeVolumeSplitterCalculator(contents + totalOverheadCount, splitterMax, volumeOffset);
				}
	
				volsMin = Math.min(esc.getVolumeCount(), volsMin);
				
				volumeForContentSheetChanged = false;
				sdc = esc;
			}

			if ( sdc.getVolumeCount()!=prvVolCount) {
				prvVolCount =  sdc.getVolumeCount();
			}
			//System.out.println("volcount "+volumeCount() + " sheets " + sheets);
			boolean ok2 = true;
			totalOverheadCount = 0;
			ret = new ArrayList<Volume>();
			int pageIndex = 0;
			
			for (int i=1;i<= sdc.getVolumeCount();i++) {
				if (splitterMax!=getVolumeMaxSize(i,  sdc.getVolumeCount())) {
					logger.warning("Implementation does not support different target volume size. All volumes must have the same target size.");
				}
				
				Volume volume = getVolume(i);
				updateVolumeContents(volume);

				totalOverheadCount += volume.getOverhead();

				{
					int targetSheetsInVolume = (i==sdc.getVolumeCount()?splitterMax:sdc.sheetsInVolume(i));
					int contentSheets = targetSheetsInVolume-volume.getOverhead();
					BreakPoint bp;
					{
						int offset = -1;
						do {
							offset++;
							bp = volBreaks.copy().nextRow(contentSheets+offset, false);
						} while (bp.getHead().length()<contentSheets && targetSheetsInVolume+offset<splitterMax);
						bp = volBreaks.nextRow(contentSheets + offset, true);
					}
					contentSheets = bp.getHead().length();
					setTargetVolSize(volume, contentSheets + volume.getOverhead());
				}
				{
					int contentSheets = volume.getTargetSize() - volume.getOverhead();
					logger.fine("Sheets  in volume " + i + ": " + (contentSheets+volume.getOverhead()) + 
							", content:" + contentSheets +
							", overhead:" + volume.getOverhead());
					PageStruct body = ps.substruct(pageIndex, contentSheets);
					pageIndex += body.countPages();
					int sheetsInVolume = body.countSheets() + volume.getOverhead();
					if (sheetsInVolume>volume.getTargetSize()) {
						ok2 = false;
						logger.fine("Error in code. Too many sheets in volume " + i + ": " + sheetsInVolume);
					}
					volume.setBody(body);
					ret.add(volume);
				}
			}
			if (volBreaks.hasNext()) {
				ok2 = false;
				logger.fine("There is more content... sheets: " + volBreaks.getRemaining() + ", pages: " +(ps.countPages()-pageIndex));
				if (!isDirty()) {
					if (volumeOffset < 1) {
						//First check to see if the page increase can will be handled automatically without increasing volume offset 
						//in the next iteration (by supplying up-to-date overhead values)
						EvenSizeVolumeSplitterCalculator esv = new EvenSizeVolumeSplitterCalculator(contents+totalOverheadCount, splitterMax, volumeOffset);
						if (esv.equals(sdc)) {
							volumeOffset++;
						}
					} else {
						logger.warning("Could not fit contents even when adding a new volume.");
					}
				}
			}
			if (!isDirty() && pageIndex==ps.countPages() && ok2) {
				//everything fits
				ok = true;
			} else if (j>9) {
				throw new RuntimeException("Failed to complete volume division.");
			} else {
				j++;
				setDirty(false);
				reformatSplitterMax = getVolumeMaxSize(1, sdc.getVolumeCount());
				logger.info("Things didn't add up, running another iteration (" + j + ")");
			}
		}
		return ret;
	}

	private void updateVolumeContents(Volume d) {
		d.setPreVolData(updateVolumeContents(d.getVolumeNumber(), true));
		d.setPostVolData(updateVolumeContents(d.getVolumeNumber(), false));
	}

	private PageStructBuilder updateVolumeContents(int volumeNumber, boolean pre) {
		DefaultContext c = new DefaultContext(volumeNumber, sdc.getVolumeCount());
		PageStructBuilder ret = null;
		try {
			ArrayList<BlockSequence> ib = new ArrayList<BlockSequence>();
			for (VolumeTemplate t : volumeTemplates) {
				if (t.appliesTo(c)) {
					for (VolumeSequence seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
						ib.addAll(seq.getBlockSequence(context, c, this));
					}
					break;
				}
			}
			ret = new PageStructBuilder(context, ib).paginate(this, c);
		} catch (PaginatorException e) {
			ret = null;
		}

		return ret;
	}
	
	/**
	 * Gets the volume max size based on the supplied information.
	 * 
	 * @param volumeNumber the volume number, one based
	 * @param volumeCount the number of volumes
	 * @return returns the maximum number of sheets in the volume
	 */
	private int getVolumeMaxSize(int volumeNumber, int volumeCount) {
		for (VolumeTemplate t : volumeTemplates) {
			if (t==null) {
				System.out.println("VOLDATA NULL");
			}
			if (t.appliesTo(new DefaultContext(volumeNumber, volumeCount))) {
				return t.getVolumeMaxSize();
			}
		}
		//TODO: don't return a fixed value
		return DEFAULT_SPLITTER_MAX;
	}	
	
	//CrossReferenceHandler
	private final Map<String, Integer> volLocations;
	private final Map<String, Integer> pageLocations;
	private final Map<Integer, Volume> volumes;
	private HashMap<Integer, Integer> volSheet;
	private PageStructBuilder ps;
	private EvenSizeVolumeSplitterCalculator sdc;
	private boolean isDirty;
	private boolean volumeForContentSheetChanged;
	
	
	@Override
	public PageStructBuilder getContents() {
		return ps;
	}
	
	private void setTargetVolSize(Volume d, int targetVolSize) {
		if (d.getTargetSize()!=targetVolSize) {
			setDirty(true);
		}
		d.setTargetVolSize(targetVolSize);
	}
	
	private Volume getVolume(int volumeNumber) {
		if (volumeNumber<1) {
			throw new IndexOutOfBoundsException("Volume must be greater than or equal to 1");
		}
		if (volumes.get(volumeNumber)==null) {
			volumes.put(volumeNumber, new Volume(volumeNumber));
			setDirty(true);
		}
		return volumes.get(volumeNumber);
	}
	
	private boolean isDirty() {
		return isDirty || volumeForContentSheetChanged;
	}

	private void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
	}

	private int updateVolumeLocation(String refid, int vol) {
		Integer v = volLocations.get(refid);
		volLocations.put(refid, vol);
		if (v!=null && v!=vol) {
			//this refid has been requested before and it changed location
			isDirty = true;
		}
		return vol;
	}
	
	private PageImpl updatePageLocation(String refid, PageImpl page) {
		Integer p = pageLocations.get(refid);
		pageLocations.put(refid, page.getPageIndex());
		if (p!=null && p!=page.getPageIndex()) {
			//this refid has been requested before and it changed location
			isDirty = true;
		}
		return page;
	}
	
	@Override
	public Integer getVolumeNumber(String refid) {
		for (int i=1; i<=sdc.getVolumeCount(); i++) {
			if (volumes.get(i)!=null) {
				if (volumes.get(i).getPreVolData()!=null && volumes.get(i).getPreVolData().getPage(refid)!=null) {
					return updateVolumeLocation(refid, i);
				}
				if (volumes.get(i).getPostVolData()!=null &&  volumes.get(i).getPostVolData().getPage(refid)!=null) {
					return updateVolumeLocation(refid, i);
				}
			}
		}
		Integer i = ps.getSheetIndex(getPage(refid));
		if (i!=null) {
			return updateVolumeLocation(refid, getVolumeForContentSheet(i));
		}
		setDirty(true);
		return null;
	}

	@Override
	public Integer getVolumeNumber(Page p) {
		Integer sheet = ps.getSheetIndex(p);
		if (sheet!=null) {
			return getVolumeForContentSheet(sheet);
		} else {
			setDirty(true);
			return null;
		}
	}

	private PageImpl getPage(String refid) {
		PageImpl ret;
		if (ps!=null && (ret=ps.getPage(refid))!=null) {
			return updatePageLocation(refid, ret);
		}
		if (sdc!=null) {
			for (int i=1; i<=sdc.getVolumeCount(); i++) {
				if (volumes.get(i)!=null) {
					if (volumes.get(i).getPreVolData()!=null && (ret=volumes.get(i).getPreVolData().getPage(refid))!=null) {
						return updatePageLocation(refid, ret);
					}
					if (volumes.get(i).getPostVolData()!=null &&  (ret=volumes.get(i).getPostVolData().getPage(refid))!=null) {
						return updatePageLocation(refid, ret);
					}
				}
			}
		}
		setDirty(true);
		return null;
	}
	
	@Override
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
			int volSize = getVolume(retVolume).getTargetSize();
			if (volSize==0) {
				volSize = sdc.sheetsInVolume(retVolume);
			}
			lastSheetInCurrentVolume += volSize;
			lastSheetInCurrentVolume -= getVolume(retVolume).getOverhead();
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
}
