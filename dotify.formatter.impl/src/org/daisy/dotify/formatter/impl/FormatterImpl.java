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
public class FormatterImpl implements Formatter {
	private final static char ZERO_WIDTH_SPACE = '\u200b';
	private final static int DEFAULT_SPLITTER_MAX = 50;
	
	private final HashMap<String, TableOfContentsImpl> tocs;
	private final HashMap<String, ContentCollectionImpl> collections;
	private final Stack<VolumeTemplate> volumeTemplates;
	
	private PageStructBuilder contentPaginator;
	private final Logger logger;
	private final CrossReferenceHandler crh;
	
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
		this.collections = new HashMap<String, ContentCollectionImpl>();
		this.volumeTemplates = new Stack<VolumeTemplate>();
		
		this.logger = Logger.getLogger(this.getClass().getCanonicalName());
		this.crh = new CrossReferenceHandler();
	}
	
	public FormatterCore newSequence(SequenceProperties p) {
		state.assertOpen();
		BlockSequence currentSequence = new BlockSequence(p.getInitialPageNumber(), context.getMasters().get(p.getMasterName()));
		blocks.push(currentSequence);
		return currentSequence;
	}

	public LayoutMasterBuilder newLayoutMaster(String name,
			LayoutMasterProperties properties) {
		return context.newLayoutMaster(name, properties);
	}

	public void open() {
		state.assertUnopened();
		state.open();
	}
	
	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}

	public VolumeTemplateBuilder newVolumeTemplate(VolumeTemplateProperties props) {
		VolumeTemplate template = new VolumeTemplate(tocs, collections, props.getCondition(), props.getSplitterMax());
		volumeTemplates.push(template);
		return template;
	}
	
	public List<VolumeTemplate> getVolumeTemplates() {
		return volumeTemplates;
	}

	public TableOfContents newToc(String tocName) {
		TableOfContentsImpl toc = new TableOfContentsImpl();
		tocs.put(tocName, toc);
		return toc;
	}
	
	public Map<String, TableOfContentsImpl> getTocs() {
		return tocs;
	}
	
	public ContentCollection newCollection(String collectionId) {
		ContentCollectionImpl collection = new ContentCollectionImpl();
		collections.put(collectionId, collection);
		return collection;
	}

	private Iterable<Volume> getVolumes() {
		contentPaginator =  new PageStructBuilder(context, blocks, collections);

		try {
			reformat(DEFAULT_SPLITTER_MAX);
		} catch (PaginatorException e) {
			throw new RuntimeException("Error while reformatting.", e);
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
			PageStructBuilder ps = crh.getContents();
			final int contents = ps.countSheets(); 
			String breakpoints = ps.buildBreakpointString();
			logger.fine("Volume break string: " + breakpoints.replace(ZERO_WIDTH_SPACE, '-'));
			BreakPointHandler volBreaks = new BreakPointHandler(breakpoints);
			int splitterMax = getVolumeMaxSize(1, crh.getExpectedVolumeCount());

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
				if (splitterMax!=getVolumeMaxSize(i, crh.getExpectedVolumeCount())) {
					logger.warning("Implementation does not support different target volume size. All volumes must have the same target size.");
				}
				preV.add((Iterable<PageSequence>) getPreVolumeContents(i));
				postV.add((Iterable<PageSequence>) getPostVolumeContents(i));
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
					bp = volBreaks.copy().nextRow(contentSheets+offset, false);
				} while (bp.getHead().length()<contentSheets && targetSheetsInVolume+offset<splitterMax);
				bp = volBreaks.nextRow(contentSheets + offset, true);
				contentSheets = bp.getHead().length();
				crh.setTargetVolSize(i, contentSheets + crh.getVolData(i).getVolOverhead());
			}
			for (int i=1;i<=crh.getExpectedVolumeCount();i++) {
				int contentSheets = crh.getVolData(i).getTargetVolSize() - crh.getVolData(i).getVolOverhead();
				logger.fine("Sheets  in volume " + i + ": " + (contentSheets+crh.getVolData(i).getVolOverhead()) + 
						", content:" + contentSheets +
						", overhead:" + crh.getVolData(i).getVolOverhead());
				PageStruct body = ps.substruct(pageIndex, contentSheets);
				pageIndex += body.countPages();
				int sheetsInVolume = body.countSheets() + crh.getVolData(i).getVolOverhead();
				if (sheetsInVolume>crh.getVolData(i).getTargetVolSize()) {
					ok2 = false;
					logger.fine("Error in code. Too many sheets in volume " + i + ": " + sheetsInVolume);
				}
				ret.add(new Volume(preV.get(i-1), body, postV.get(i-1)));
			}
			if (volBreaks.hasNext()) {
				ok2 = false;
				logger.fine("There is more content... sheets: " + volBreaks.getRemaining() + ", pages: " +(ps.countPages()-pageIndex));
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
			if (!crh.isDirty() && pageIndex==ps.countPages() && ok2) {
				//everything fits
				ok = true;
			} else if (j>9) {
				throw new RuntimeException("Failed to complete volume division.");
			} else {
				j++;
				crh.setDirty(false);
				try {
					reformat(getVolumeMaxSize(1, crh.getExpectedVolumeCount()));
				} catch (PaginatorException e) {
					throw new RuntimeException("Error while reformatting.", e);
				}
				logger.info("Things didn't add up, running another iteration (" + j + ")");
			}
		}
		return ret;
	}
	
	private void reformat(int splitterMax) throws PaginatorException {
		crh.setContents(contentPaginator.paginate(crh, new DefaultContext(null, null)), splitterMax);
	}

	private PageStruct getPreVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, true);
	}

	private PageStruct getPostVolumeContents(int volumeNumber) {
		return getVolumeContents(volumeNumber, false);
	}

	private PageStruct getVolumeContents(int volumeNumber, boolean pre) {
		DefaultContext c = new DefaultContext(volumeNumber, crh.getExpectedVolumeCount());
		PageStructBuilder ret = null;
		try {
			Iterable<BlockSequence> ib = formatVolumeContents(crh, pre, c);
			ret = new PageStructBuilder(context, ib, collections).paginate(crh, c);
		} catch (IOException e) {
			ret = null;
		} catch (PaginatorException e) {
			ret = null;
		}

		if (pre) {
			crh.setPreVolData(volumeNumber, ret);
		} else {
			crh.setPostVolData(volumeNumber, ret);
		}
		return ret;
	}

	private Iterable<BlockSequence> formatVolumeContents(CrossReferences crh, boolean pre, DefaultContext c) throws IOException {
		ArrayList<BlockSequence> ib = new ArrayList<BlockSequence>();
		for (VolumeTemplate t : volumeTemplates) {
			if (t.appliesTo(c)) {
				for (VolumeSequence seq : (pre?t.getPreVolumeContent():t.getPostVolumeContent())) {
					ib.addAll(seq.getBlockSequence(context, c, crh));
				}
				break;
			}
		}
		return ib;
	}
	
	/**
	 * Gets the volume max size based on the supplied information.
	 * 
	 * @param volumeNumber the volume number, one based
	 * @param volumeCount the number of volumes
	 * @return returns the maximum number of sheets in the volume
	 */
	public int getVolumeMaxSize(int volumeNumber, int volumeCount) {
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

	public void write(PagedMediaWriter writer) {
		WriterHandler wh = new WriterHandler();
		wh.write(getVolumes(), writer);
		try {
			writer.close();
		} catch (IOException e) {
		}
	}

}
