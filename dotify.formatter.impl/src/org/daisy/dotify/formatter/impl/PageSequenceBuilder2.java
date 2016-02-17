package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.formatter.BlockPosition;
import org.daisy.dotify.api.formatter.FallbackRule;
import org.daisy.dotify.api.formatter.FormattingTypes.BreakBefore;
import org.daisy.dotify.api.formatter.FormattingTypes.Keep;
import org.daisy.dotify.api.formatter.MarginRegion;
import org.daisy.dotify.api.formatter.MarkerIndicator;
import org.daisy.dotify.api.formatter.MarkerIndicatorRegion;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.formatter.RenameFallbackRule;
import org.daisy.dotify.api.formatter.RenderingScenario;
import org.daisy.dotify.api.translator.Translatable;
import org.daisy.dotify.api.translator.TranslationException;
import org.daisy.dotify.common.collection.SplitList;
import org.daisy.dotify.common.layout.SplitPoint;
import org.daisy.dotify.common.layout.SplitPointData;
import org.daisy.dotify.common.layout.SplitPointHandler;
import org.daisy.dotify.common.layout.Supplements;

class PageSequenceBuilder2 {
	private final Map<String, PageImpl> pageReferences;
	private final FormatterContext context;
	private final BlockSequence seq;
	private final CrossReferenceHandler crh;
	private final PageAreaContent staticAreaContent;
	private final PageAreaProperties areaProps;

	private int keepNextSheets;
	private ContentCollectionImpl collection;
	private BlockContext blockContext;
	private final PageSequence ps;

	PageSequenceBuilder2(PageSequence ps, CrossReferenceHandler crh, BlockSequence seq, Map<String, PageImpl> pageReferences, FormatterContext context, CrossReferences refs, DefaultContext rcontext) {
		this.ps = ps;
		this.pageReferences = pageReferences;
		this.context = context;
		this.seq = seq;
		this.crh = crh;

		this.collection = null;
		this.areaProps = seq.getLayoutMaster().getPageArea();
		if (this.areaProps!=null) {
			this.collection = context.getCollections().get(areaProps.getCollectionId());
		}
		this.keepNextSheets = 0;
		
		this.blockContext = new BlockContext(seq.getLayoutMaster().getFlowWidth(), refs, rcontext, context);
		this.staticAreaContent = new PageAreaContent(seq.getLayoutMaster().getPageAreaBuilder(), blockContext);
	}

	private void newPage() {
		ps.addPage(new PageImpl(ps.getLayoutMaster(), context, ps, ps.getPageCount()+ps.getPageNumberOffset(), staticAreaContent.getBefore(), staticAreaContent.getAfter()));
		if (keepNextSheets>0) {
			currentPage().setAllowsVolumeBreak(false);
		}
		if (!ps.getLayoutMaster().duplex() || ps.getPageCount()%2==0) {
			if (keepNextSheets>0) {
				keepNextSheets--;
			}
		}
	}

	private void setKeepWithPreviousSheets(int value) {
		currentPage().setKeepWithPreviousSheets(value);
	}

	private void setKeepWithNextSheets(int value) {
		keepNextSheets = Math.max(value, keepNextSheets);
		if (keepNextSheets>0) {
			currentPage().setAllowsVolumeBreak(false);
		}
	}
	
	private PageImpl currentPage() {
		return ps.peek();
	}

	/**
	 * Space used, in rows
	 * 
	 * @return
	 */
	private int spaceUsedOnPage(int offs) {
		return currentPage().spaceUsedOnPage(offs);
	}

	private void newRow(RowImpl row) {
		if (spaceUsedOnPage(1) > currentPage().getFlowHeight()) {
			newPage();
		}
		currentPage().newRow(row);
	}

	private void insertIdentifier(String id) {
		crh.setPageNumber(id, currentPage().getPageIndex() + 1);
		currentPage().addIdentifier(id);
		if (pageReferences.put(id, currentPage())!=null) {
			throw new IllegalArgumentException("Identifier not unique: " + id);
		}
	}
	
	private int getTotalMarginRegionWidth() {
		int mw = 0;
		for (MarginRegion mr : seq.getLayoutMaster().getTemplate(1).getLeftMarginRegion()) {
			mw += mr.getWidth();
		}
		for (MarginRegion mr : seq.getLayoutMaster().getTemplate(1).getRightMarginRegion()) {
			mw += mr.getWidth();
		}
		return mw;
	}
	
	private List<RowGroupSequence> buildRowGroups() {
		PageSequenceRecorder rec = new PageSequenceRecorder();

		//TODO: This assumes that all page templates have margin regions that are of the same width 
		final int mw = getTotalMarginRegionWidth(); 
		BlockContext bc = new BlockContext(seq.getLayoutMaster().getFlowWidth() - mw, blockContext.getRefs(), blockContext.getContext(), blockContext.getFcontext());
		for (Block g : seq)  {
			try {
				AbstractBlockContentManager bcm = rec.processBlock(g, bc);

				if (rec.data.isDataGroupsEmpty() || (g.getBreakBeforeType()==BreakBefore.PAGE && !rec.data.isDataEmpty()) || g.getVerticalPosition()!=null) {
					rec.data.newRowGroupSequence(g.getVerticalPosition(), new RowImpl("", bcm.getLeftMarginParent(), bcm.getRightMarginParent()));
					rec.data.keepWithNext = -1;
				}
				List<RowImpl> rl1 = bcm.getCollapsiblePreContentRows();
				if (!rl1.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(ps.getLayoutMaster().getRowSpacing(), rl1).
											collapsible(true).skippable(false).breakable(false).build());
				}
				List<RowImpl> rl2 = bcm.getInnerPreContentRows();
				if (!rl2.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(ps.getLayoutMaster().getRowSpacing(), rl2).
											collapsible(false).skippable(false).breakable(false).build());
				}
				
				if (bcm.getRowCount()==0) { //TODO: Does this interfere with collapsing margins? 
					if (!bcm.getGroupAnchors().isEmpty() || !bcm.getGroupMarkers().isEmpty() || !"".equals(g.getIdentifier())
							|| g.getKeepWithNextSheets()>0 || g.getKeepWithPreviousSheets()>0 ) {
						RowGroup.Builder rgb = new RowGroup.Builder(ps.getLayoutMaster().getRowSpacing(), new ArrayList<RowImpl>());
						setProperties(rgb, bcm, g);
						rec.data.addRowGroup(rgb.build());
					}
				}
	
				int i = 0;
				List<RowImpl> rl3 = bcm.getPostContentRows();
				OrphanWidowControl owc = new OrphanWidowControl(g.getRowDataProperties().getOrphans(),
																g.getRowDataProperties().getWidows(), 
																bcm.getRowCount());
				for (RowImpl r : bcm) {
					i++;
					r.setAdjustedForMargin(true);
					if (i==bcm.getRowCount()) {
						//we're at the last line, this should be kept with the next block's first line
						rec.data.keepWithNext = g.getKeepWithNext();
					}
					RowGroup.Builder rgb = new RowGroup.Builder(ps.getLayoutMaster().getRowSpacing()).add(r).
							collapsible(false).skippable(false).breakable(
									r.allowsBreakAfter()&&
									owc.allowsBreakAfter(i-1)&&
									rec.data.keepWithNext<=0 &&
									(Keep.AUTO==g.getKeepType() || i==bcm.getRowCount()) &&
									(i<bcm.getRowCount() || rl3.isEmpty())
									);
					if (i==1) { //First item
						setProperties(rgb, bcm, g);
					}
					rec.data.addRowGroup(rgb.build());
					rec.data.keepWithNext--;
				}
				if (!rl3.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(ps.getLayoutMaster().getRowSpacing(), rl3).
						collapsible(false).skippable(false).breakable(rec.data.keepWithNext<0).build());
				}
				List<RowImpl> rl4 = bcm.getSkippablePostContentRows();
				if (!rl4.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(ps.getLayoutMaster().getRowSpacing(), rl4).
						collapsible(true).skippable(true).breakable(rec.data.keepWithNext<0).build());
				}
			} catch (Exception e) {
				rec.invalidateScenario(e);
			}
		}
		rec.finishBlockProcessing();
		return rec.data.getRowGroupSequences();
	}
	
	private void setProperties(RowGroup.Builder rgb, AbstractBlockContentManager bcm, Block g) {
		if (!"".equals(g.getIdentifier())) { 
			rgb.identifier(g.getIdentifier());
		}
		rgb.markers(bcm.getGroupMarkers());
		rgb.anchors(bcm.getGroupAnchors());
		rgb.keepWithNextSheets(g.getKeepWithNextSheets());
		rgb.keepWithPreviousSheets(g.getKeepWithPreviousSheets());
	}
	
	boolean paginate() throws PaginatorException  {
		
		List<RowGroupSequence> dataGroups = buildRowGroups();
		
		SplitPointHandler<RowGroup> sph = new SplitPointHandler<>();
		SplitPoint<RowGroup> res = null;
		SplitPointData<RowGroup> spd;
		
		for (RowGroupSequence rgs : dataGroups) {
			List<RowGroup> data = rgs.getGroup();
			if (rgs.getBlockPosition()!=null) {
				if (ps.isSequenceEmpty()) {
					newPage();
				}
				float size = 0;
				for (RowGroup g : data) {
					size += g.getUnitSize();
				}
				int pos = calculateVerticalSpace(rgs.getBlockPosition(), (int)Math.ceil(size));
				for (int i = 0; i < pos; i++) {
					RowImpl ri = rgs.getEmptyRow();
					newRow(new RowImpl(ri.getChars(), ri.getLeftMargin(), ri.getRightMargin()));
				}
			} else {
				newPage();
			}
			boolean force = false;
			while (data.size()>0) {
				SplitList<RowGroup> sl = SplitPointHandler.trimLeading(data);
				for (RowGroup rg : sl.getFirstPart()) {
					addProperties(rg);
				}
				data = sl.getSecondPart();
				spd = new SplitPointData<>(data, new CollectionData(blockContext));
				res = sph.split(currentPage().getFlowHeight(), force, spd);
				force = res.getHead().size()==0;
				data = res.getTail();
				for (RowGroup rg : res.getHead()) {
					addProperties(rg);
					for (RowImpl r : rg.getRows()) { 
						if (r.shouldAdjustForMargin()) {
							// clone the row as not to append the margins twice
							r = RowImpl.withRow(r);
							for (MarginRegion mr : currentPage().getPageTemplate().getLeftMarginRegion()) {
								r.setLeftMargin(getMarginRegionValue(mr, r, false).append(r.getLeftMargin()));
							}
							for (MarginRegion mr : currentPage().getPageTemplate().getRightMarginRegion()) {
								r.setRightMargin(r.getRightMargin().append(getMarginRegionValue(mr, r, true)));
							}
						}
						currentPage().newRow(r);
					}
				}
				for (RowGroup rg : res.getDiscarded()) {
					addProperties(rg);
				}
				for (RowGroup rg : res.getSupplements()) {
					currentPage().addToPageArea(rg.getRows());
				}
				if (ps.getLayoutMaster().getPageArea()!=null && collection!=null && currentPage().pageAreaSpaceNeeded() > ps.getLayoutMaster().getPageArea().getMaxHeight()) {
					reassignCollection();
					return false;
				}
				if (data.size()>0) {
					newPage();
				}
			}
		}
		return true;
	}
	
	private MarginProperties getMarginRegionValue(MarginRegion mr, RowImpl r, boolean rightSide) throws PaginatorException {
		String ret = "";
		int w = mr.getWidth();
		if (mr instanceof MarkerIndicatorRegion) {
			ret = firstMarkerForRow(r, (MarkerIndicatorRegion)mr);
			if (ret.length()>0) {
				try {
					ret = context.getDefaultTranslator().translate(Translatable.text(ret).build()).getTranslatedRemainder();
				} catch (TranslationException e) {
					throw new PaginatorException("Failed to translate: " + ret, e);
				}
			}
			boolean spaceOnly = ret.length()==0;
			if (ret.length()<w) {
				StringBuilder sb = new StringBuilder();
				if (rightSide) {
					while (sb.length()<w-ret.length()) { sb.append(context.getSpaceCharacter()); }
					sb.append(ret);
				} else {
					sb.append(ret);				
					while (sb.length()<w) { sb.append(context.getSpaceCharacter()); }
				}
				ret = sb.toString();
			} else if (ret.length()>w) {
				throw new PaginatorException("Cannot fit " + ret + " into a margin-region of size "+ mr.getWidth());
			}
			return new MarginProperties(ret, spaceOnly);
		} else {
			throw new PaginatorException("Unsupported margin-region type: " + mr.getClass().getName());
		}
	}
	
	private String firstMarkerForRow(RowImpl r, MarkerIndicatorRegion mrr) {
		for (MarkerIndicator mi : mrr.getIndicators()) {
			if (r.hasMarkerWithName(mi.getName())) {
				return mi.getIndicator();
			}
		}
		return "";
	}
	
	private void addProperties(RowGroup rg) {
		if (rg.getIdentifier()!=null) {
			insertIdentifier(rg.getIdentifier());
		}
		currentPage().addMarkers(rg.getMarkers());
		//TODO: addGroupAnchors
		setKeepWithNextSheets(rg.getKeepWithNextSheets());
		setKeepWithPreviousSheets(rg.getKeepWithPreviousSheets());
	}
	
	private void reassignCollection() throws PaginatorException {
		//reassign collection
		if (areaProps!=null) {
			int i = 0;
			for (FallbackRule r : areaProps.getFallbackRules()) {
				i++;
				if (r instanceof RenameFallbackRule) {
					collection = context.getCollections().remove(r.applyToCollection());
					if (context.getCollections().put(((RenameFallbackRule)r).getToCollection(), collection)!=null) {
						throw new PaginatorException("Fallback id already in use:" + ((RenameFallbackRule)r).getToCollection());
					}							
				} else {
					throw new PaginatorException("Unknown fallback rule: " + r);
				}
			}
			if (i==0) {
				throw new PaginatorException("Failed to fit collection '" + areaProps.getCollectionId() + "' within the page-area boundaries, and no fallback was defined.");
			}
		}
	}
	
	private class CollectionData implements Supplements<RowGroup> {
		private boolean first;
		private final BlockContext c;
		private final Map<String, RowGroup> map;
		
		private CollectionData(BlockContext c) {
			this.c = c;
			this.first = true;
			this.map = new HashMap<>();
		}

		@Override
		public RowGroup get(String id) {
			if (collection!=null) {
				RowGroup ret = map.get(id);
				if (ret==null) {
					RowGroup.Builder b = new RowGroup.Builder(ps.getLayoutMaster().getRowSpacing());
					for (Block g : collection.getBlocks(id)) {
						AbstractBlockContentManager bcm = g.getBlockContentManager(c);
						b.addAll(bcm.getCollapsiblePreContentRows());
						b.addAll(bcm.getInnerPreContentRows());
						for (RowImpl r : bcm) {
							b.add(r);
						}
						b.addAll(bcm.getPostContentRows());
						b.addAll(bcm.getSkippablePostContentRows());
					}
					if (first) {
						b.overhead(currentPage().staticAreaSpaceNeeded());
						first = false;
					}
					ret = b.build();
					map.put(id, ret);
				} 
				return ret;
			} else {
				return null;
			}
		}
		
	}
	
	private int calculateVerticalSpace(BlockPosition p, int blockSpace) {
		if (p != null) {
			int pos = p.getPosition().makeAbsolute(currentPage().getFlowHeight());
			int t = pos - spaceUsedOnPage(0);
			if (t > 0) {
				int advance = 0;
				switch (p.getAlignment()) {
				case BEFORE:
					advance = t - blockSpace;
					break;
				case CENTER:
					advance = t - blockSpace / 2;
					break;
				case AFTER:
					advance = t;
					break;
				}
				return (int)Math.floor(advance / ps.getLayoutMaster().getRowSpacing());
			}
		}
		return 0;
	}

}
