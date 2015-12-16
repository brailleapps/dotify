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
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.common.io.StateObject;
import org.daisy.dotify.common.text.BreakPoint;
import org.daisy.dotify.common.text.BreakPointHandler;


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
	
	//CrossReferenceHandler
	private final Map<Integer, Volume> volumes;
	private PageStruct ps;
	private EvenSizeVolumeSplitterCalculator sdc;
	private boolean isDirty;
	private boolean volumeForContentSheetChanged;
	private CrossReferenceHandler crh;

	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslatorFactoryMakerService translatorFactory, String locale, String mode) {
		this(new FormatterContext(translatorFactory, locale, mode));
	}
	
	public FormatterImpl(FormatterContext context) {
		this.context = context;
		this.blocks = new Stack<>();
		this.state = new StateObject();
		this.tocs = new HashMap<>();
		this.volumeTemplates = new Stack<>();
		
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		
		//CrossReferenceHandler
		this.volumes = new HashMap<>();
		this.isDirty = false;
		this.volumeForContentSheetChanged = false;
		this.crh = new CrossReferenceHandler();
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
		ArrayList<Volume> ret = new ArrayList<>();
		ArrayList<AnchorData> ad;

		while (!ok) {
			try {
				this.ps = contentPaginator.paginate(crh, this, new DefaultContext(null, null));
			} catch (PaginatorException e) {
				throw new RuntimeException("Error while reformatting.", e);
			}
			final int contents = PageStruct.countSheets(ps); 
			this.sdc = new EvenSizeVolumeSplitterCalculator(contents, reformatSplitterMax);
			// make a preliminary calculation based on contents only
			
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
			ret = new ArrayList<>();
			int pageIndex = 0;
			
			for (int i=1;i<= sdc.getVolumeCount();i++) {
				if (splitterMax!=getVolumeMaxSize(i,  sdc.getVolumeCount())) {
					logger.warning("Implementation does not support different target volume size. All volumes must have the same target size.");
				}
				
				Volume volume = getVolume(i);
				ad = new ArrayList<>();

				volume.setPreVolData(updateVolumeContents(i, ad, true));

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
					Iterable<PageSequence> body = ps.substruct(pageIndex, contentSheets);
					int pageCount = PageStruct.countPages(body);
					ps.setVolumeScope(i, pageIndex, pageIndex+pageCount);
					pageIndex += pageCount;
					int sheetsInVolume = PageStruct.countSheets(body) + volume.getOverhead();
					if (sheetsInVolume>volume.getTargetSize()) {
						ok2 = false;
						logger.fine("Error in code. Too many sheets in volume " + i + ": " + sheetsInVolume);
					}
					for (PageSequence ps : body) {
						for (PageImpl p : ps.getPages()) {
							for (String id : p.getIdentifiers()) {
								crh.setVolumeNumber(id, i);
							}
							if (p.getAnchors().size()>0) {
								ad.add(new AnchorData(p.getPageIndex(), p.getAnchors()));
							}
						}
					}
					volume.setBody(body);
					
					volume.setPostVolData(updateVolumeContents(i, ad, false));
					crh.setAnchorData(i, ad);

					ret.add(volume);
				}
			}
			if (volBreaks.hasNext()) {
				ok2 = false;
				logger.fine("There is more content... sheets: " + volBreaks.getRemaining() + ", pages: " +(PageStruct.countPages(ps)-pageIndex));
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
			if (!isDirty() && pageIndex==PageStruct.countPages(ps) && ok2) {
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


	private PageStruct updateVolumeContents(int volumeNumber, ArrayList<AnchorData> ad, boolean pre) {
		DefaultContext c = new DefaultContext(volumeNumber, sdc.getVolumeCount());
		PageStruct ret = null;
		try {
			ArrayList<BlockSequence> ib = new ArrayList<>();
			for (VolumeTemplate t : volumeTemplates) {
				if (t.appliesTo(c)) {
					for (VolumeSequence seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
						BlockSequence s = seq.getBlockSequence(context, c, this);
						if (s!=null) {
							ib.add(s);
						}
					}
					break;
				}
			}
			ret = new PageStructBuilder(context, ib).paginate(crh, this, c);
			for (PageSequence ps : ret) {
				for (PageImpl p : ps.getPages()) {
					for (String id : p.getIdentifiers()) {
						crh.setVolumeNumber(id, volumeNumber);
					}
					if (p.getAnchors().size()>0) {
						ad.add(new AnchorData(p.getPageIndex(), p.getAnchors()));
					}
				}
			}
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
	
	public PageStruct getContents() {
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
		return isDirty || volumeForContentSheetChanged || crh.isDirty();
	}

	private void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		crh.setDirty(isDirty);
	}
	
	@Override
	public Integer getVolumeNumber(String refid) {
		return crh.getVolumeNumber(refid);
	}

	@Override
	public Integer getPageNumber(String refid) {
		return crh.getPageNumber(refid);
	}

	@Override
	public int getVolumeCount() {
		return sdc.getVolumeCount();
	}

	@Override
	public Iterable<AnchorData> getAnchorData(int volume) {
		return crh.getAnchorData(volume);
	}

}
