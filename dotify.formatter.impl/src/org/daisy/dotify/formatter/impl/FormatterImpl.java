package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;

import org.daisy.dotify.api.formatter.ContentCollection;
import org.daisy.dotify.api.formatter.Formatter;
import org.daisy.dotify.api.formatter.FormatterSequence;
import org.daisy.dotify.api.formatter.LayoutMasterBuilder;
import org.daisy.dotify.api.formatter.LayoutMasterProperties;
import org.daisy.dotify.api.formatter.SequenceProperties;
import org.daisy.dotify.api.formatter.TableOfContents;
import org.daisy.dotify.api.formatter.VolumeTemplateBuilder;
import org.daisy.dotify.api.formatter.VolumeTemplateProperties;
import org.daisy.dotify.api.translator.BrailleTranslatorFactoryMakerService;
import org.daisy.dotify.api.translator.TextBorderFactoryMakerService;
import org.daisy.dotify.api.writer.PagedMediaWriter;
import org.daisy.dotify.common.io.StateObject;
import org.daisy.dotify.common.layout.SplitPoint;
import org.daisy.dotify.common.layout.SplitPointHandler;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson
 */
public class FormatterImpl implements Formatter {
	//private final static char ZERO_WIDTH_SPACE = '\u200b';
	private final static int DEFAULT_SPLITTER_MAX = 50;
	
	private final HashMap<String, TableOfContentsImpl> tocs;
	private final Stack<VolumeTemplate> volumeTemplates;
	private final Logger logger;
	
	private final StateObject state;
	private final FormatterContext context;
	private final Stack<BlockSequence> blocks;
	
	//CrossReferenceHandler
	private final Map<Integer, Volume> volumes;
	private final VolumeSplitter splitter;
	private boolean isDirty;
	private CrossReferenceHandler crh;

	/**
	 * Creates a new formatter
	 */
	public FormatterImpl(BrailleTranslatorFactoryMakerService translatorFactory, TextBorderFactoryMakerService tbf, String locale, String mode) {
		this(new FormatterContext(translatorFactory, tbf, locale, mode));
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
		this.crh = new CrossReferenceHandler();
		this.splitter = new EvenSizeVolumeSplitter(crh.getVariables());
	}
	
	@Override
	public FormatterSequence newSequence(SequenceProperties p) {
		state.assertOpen();
		BlockSequence currentSequence = new BlockSequence(context, p.getInitialPageNumber(), context.getMasters().get(p.getMasterName()));
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
		VolumeTemplate template = new VolumeTemplate(context, tocs, props.getCondition(), props.getSplitterMax());
		volumeTemplates.push(template);
		return template;
	}

	@Override
	public TableOfContents newToc(String tocName) {
		TableOfContentsImpl toc = new TableOfContentsImpl(context);
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
		
		ArrayList<Volume> ret = new ArrayList<>();
		ArrayList<AnchorData> ad;
		PageStruct ps;
		VariablesHandler vh = crh.getVariables();
		SplitPointHandler<Sheet> volSplitter = new SplitPointHandler<>();
		while (!ok) {
			//BreakPointHandler volBreaks;
			List<Sheet> units;
			try {
				ps = contentPaginator.paginate(crh, new DefaultContext(null, null));
				//String breakpoints = ps.buildBreakpointString();
				//logger.fine(breakpoints.replace(ZERO_WIDTH_SPACE, '-'));
				//volBreaks = new BreakPointHandler(breakpoints);
				units = ps.buildSplitPoints();
				//logger.fine(PageStruct.toString(units));
			} catch (PaginatorException e) {
				throw new RuntimeException("Error while reformatting.", e);
			}

			// make a preliminary calculation based on contents only
			splitter.setSplitterMax(getVolumeMaxSize(1,  vh.getVolumeCount()));
			splitter.updateSheetCount(ps.getSheetCount() + totalOverheadCount);

			//System.out.println("volcount "+volumeCount() + " sheets " + sheets);
			boolean ok2 = true;
			totalOverheadCount = 0;
			ret = new ArrayList<>();
			int pageIndex = 0;
			
			for (int i=1;i<= vh.getVolumeCount();i++) {
				if (splitter.getSplitterMax()!=getVolumeMaxSize(i,  vh.getVolumeCount())) {
					logger.warning("Implementation does not support different target volume size. All volumes must have the same target size.");
				}
				
				Volume volume = getVolume(i);
				ad = new ArrayList<>();

				volume.setPreVolData(updateVolumeContents(i, ad, true));

				totalOverheadCount += volume.getOverhead();

				{
					/*
					int contentSheets = getBreakPoint(volBreaks, 
							(i==vh.getVolumeCount()?splitter.getSplitterMax():splitter.sheetsInVolume(i)),
							volume.getOverhead());*/
					SplitPoint<Sheet> sp = getSplitPoint(volSplitter,
							(i==vh.getVolumeCount()?splitter.getSplitterMax():splitter.sheetsInVolume(i)),
							volume.getOverhead(), 
							units);
					
					int contentSheets = sp.getHead().size();
					units = sp.getTail();
					setTargetVolSize(volume, contentSheets + volume.getOverhead());
					logger.fine("Sheets  in volume " + i + ": " + (contentSheets+volume.getOverhead()) + 
							", content:" + contentSheets +
							", overhead:" + volume.getOverhead());
					
					Iterable<PageSequence> body = sequencesFromSheets(sp.getHead());
					int pageCount = PageStruct.countPages(body);
					ps.setVolumeScope(i, pageIndex, pageIndex+pageCount);
					pageIndex += pageCount;
					for (PageSequence seq : body) {
						for (PageImpl p : seq.getPages()) {
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
			if (!units.isEmpty()) {
				ok2 = false;
				logger.warning("There is more content... sheets: " + units + ", pages: " +(PageStruct.countPages(ps)-pageIndex));
				if (!isDirty()) {
					splitter.adjustVolumeCount(ps.getSheetCount()+totalOverheadCount);
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
				logger.info("Things didn't add up, running another iteration (" + j + ")");
			}
		}
		return ret;
	}
	
	private static Iterable<PageSequence> sequencesFromSheets(List<Sheet> sheets) {
		PageStruct ret = new PageStruct();
		PageSequence currentSeq = null;
		for (Sheet s : sheets) {
			for (PageImpl p : s.getPages()) {
				if (ret.empty() || currentSeq!=p.getSequenceParent()) {
					currentSeq = p.getSequenceParent();
					ret.add(new PageSequence(ret, currentSeq.getLayoutMaster(), currentSeq.getPageNumberOffset()));
				}
				((PageSequence)ret.peek()).addPage(p);
			}
		}
		return ret;
	}

	/*
	private int getBreakPoint(BreakPointHandler volBreaks, int targetSheetsInVolume, int overhead) {
		int contentSheets = targetSheetsInVolume-overhead;
		BreakPoint bp;
		{
			int offset = -1;
			do {
				offset++;
				bp = volBreaks.copy().nextRow(contentSheets+offset, false);
			} while (bp.getHead().length()<contentSheets && targetSheetsInVolume+offset<splitter.getSplitterMax());
			bp = volBreaks.nextRow(contentSheets + offset, true);
		}
		return bp.getHead().length();
	}*/
	
	private SplitPoint<Sheet> getSplitPoint(SplitPointHandler<Sheet> volBreaks, int targetSheetsInVolume, int overhead, List<Sheet> data) {
		int contentSheets = targetSheetsInVolume-overhead;
		SplitPoint<Sheet> bp;
		{
			int offset = -1;
			do {
				offset++;
				bp = volBreaks.split(contentSheets+offset, false, data);
			} while (bp.getHead().size()<contentSheets && targetSheetsInVolume+offset<splitter.getSplitterMax());
			bp = volBreaks.split(contentSheets + offset, true, data);
		}
		return bp;
	}

	private PageStruct updateVolumeContents(int volumeNumber, ArrayList<AnchorData> ad, boolean pre) {
		DefaultContext c = new DefaultContext(volumeNumber, crh.getVariables().getVolumeCount());
		PageStruct ret = null;
		try {
			ArrayList<BlockSequence> ib = new ArrayList<>();
			for (VolumeTemplate t : volumeTemplates) {
				if (t.appliesTo(c)) {
					for (VolumeSequence seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
						BlockSequence s = seq.getBlockSequence(context, c, crh);
						if (s!=null) {
							ib.add(s);
						}
					}
					break;
				}
			}
			ret = new PageStructBuilder(context, ib).paginate(crh, c);
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
			if (t.appliesTo(new DefaultContext.Builder()
						.currentVolume(volumeNumber)
						.volumeCount(volumeCount)
						.build())) {
				return t.getVolumeMaxSize();
			}
		}
		//TODO: don't return a fixed value
		return DEFAULT_SPLITTER_MAX;
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
		return isDirty || crh.isDirty();
	}

	private void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		crh.setDirty(isDirty);
	}

}
