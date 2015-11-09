package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.formatter.BlockPosition;
import org.daisy.dotify.api.formatter.FallbackRule;
import org.daisy.dotify.api.formatter.FormattingTypes.BreakBefore;
import org.daisy.dotify.api.formatter.FormattingTypes.Keep;
import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.formatter.RenameFallbackRule;
import org.daisy.dotify.common.collection.SplitList;
import org.daisy.dotify.common.layout.SplitPoint;
import org.daisy.dotify.common.layout.SplitPointData;
import org.daisy.dotify.common.layout.SplitPointHandler;
import org.daisy.dotify.common.layout.Supplements;

class PageSequenceBuilder2 extends PageSequence {
	private final Map<String, PageImpl> pageReferences;
	private final FormatterContext context;
	private final BlockSequence seq;
	private final CrossReferenceHandler crh;
	private final PageAreaContent staticAreaContent;
	private final PageAreaProperties areaProps;

	private int keepNextSheets;
	private PageImpl nextPage;
	private ContentCollectionImpl collection;
	private BlockContext blockContext;

	PageSequenceBuilder2(CrossReferenceHandler crh, BlockSequence seq, Map<String, PageImpl> pageReferences, FormatterContext context, 
						int pagesOffset, CrossReferences refs, DefaultContext rcontext) {
		super(seq.getLayoutMaster());
		this.pageReferences = pageReferences;
		this.context = context;
		this.seq = seq;
		this.crh = crh;
		if (seq.getInitialPageNumber()!=null) {
			this.pagesOffset = seq.getInitialPageNumber() - 1;
		} else {
			this.pagesOffset = pagesOffset;
		}

		this.collection = null;
		this.areaProps = seq.getLayoutMaster().getPageArea();
		if (this.areaProps!=null) {
			this.collection = context.getCollections().get(areaProps.getCollectionId());
		}
		this.keepNextSheets = 0;
		this.nextPage = null;
		
		this.blockContext = new BlockContext(seq.getLayoutMaster().getFlowWidth(), refs, rcontext, context);
		this.staticAreaContent = new PageAreaContent(seq.getLayoutMaster().getPageAreaBuilder(), blockContext);
	}

	private void newPage() {
		if (nextPage!=null) {
			pages.push(nextPage);
			nextPage = null;
		} else {
			pages.push(new PageImpl(master, context, this, pages.size()+pagesOffset, staticAreaContent.getBefore(), staticAreaContent.getAfter()));
		}
		if (keepNextSheets>0) {
			currentPage().setAllowsVolumeBreak(false);
		}
		if (!getLayoutMaster().duplex() || getPageCount()%2==0) {
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
		if (nextPage!=null) {
			return nextPage;
		} else {
			return pages.peek();
		}
	}
	
	int currentPageNumber() {
		return currentPage().getPageIndex()+1;
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
		if (spaceUsedOnPage(1) > currentPage().getFlowHeight() || nextPage != null) {
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
	
	private List<RowGroupSequence> buildRowGroups() {
		List<RowGroupSequence> dataGroups = new ArrayList<>();
		List<RowGroup> data = new ArrayList<>();
		int keepWithNext = 0;
		for (Block g : seq)  {
			BlockContentManager bcm = g.getBlockContentManager(blockContext);
			if (dataGroups.isEmpty() || g.getBreakBeforeType()==BreakBefore.PAGE || g.getVerticalPosition()!=null) {
				data = new ArrayList<>();
				dataGroups.add(new RowGroupSequence(data, g.getVerticalPosition(), new RowImpl("", bcm.getLeftMarginParent(), bcm.getRightMarginParent())));
				keepWithNext = -1;
			}
			List<RowImpl> rl1 = bcm.getCollapsiblePreContentRows();
			if (!rl1.isEmpty()) {
				data.add(new RowGroup.Builder(master.getRowSpacing(), rl1).
										collapsible(true).skippable(false).breakable(false).build());
			}
			List<RowImpl> rl2 = bcm.getInnerPreContentRows();
			if (!rl2.isEmpty()) {
				data.add(new RowGroup.Builder(master.getRowSpacing(), rl2).
										collapsible(false).skippable(false).breakable(false).build());
			}
			
			if (bcm.getRowCount()==0) { //TODO: Does this interfere with collapsing margins? 
				if (!bcm.getGroupAnchors().isEmpty() || !bcm.getGroupMarkers().isEmpty() || !"".equals(g.getIdentifier())
						|| g.getKeepWithNextSheets()>0 || g.getKeepWithPreviousSheets()>0 ) {
					RowGroup.Builder rgb = new RowGroup.Builder(master.getRowSpacing(), new ArrayList<RowImpl>());
					setProperties(rgb, bcm, g);
					data.add(rgb.build());
				}
			}

			int i = 0;
			List<RowImpl> rl3 = bcm.getPostContentRows();
			for (RowImpl r : bcm) {
				i++;
				if (i==bcm.getRowCount()) {
					//we're at the last line, this should be kept with the next block's first line
					keepWithNext = g.getKeepWithNext();
				}
				RowGroup.Builder rgb = new RowGroup.Builder(master.getRowSpacing()).add(r).
						//FIXME: orphans/widows
						collapsible(false).skippable(false).breakable(
								keepWithNext<=0 &&
								(Keep.AUTO==g.getKeepType() || (i==bcm.getRowCount() && rl3.isEmpty())));
				if (i==1) { //First item
					setProperties(rgb, bcm, g);
				}
				data.add(rgb.build());
				keepWithNext--;
			}
			if (!rl3.isEmpty()) {
				data.add(new RowGroup.Builder(master.getRowSpacing(), rl3).
					collapsible(false).skippable(false).breakable(false).build());
			}
			List<RowImpl> rl4 = bcm.getSkippablePostContentRows();
			if (!rl4.isEmpty()) {
				data.add(new RowGroup.Builder(master.getRowSpacing(), rl4).
					collapsible(true).skippable(true).breakable(keepWithNext<0).build());
			}
		}
		return dataGroups;
	}
	
	private void setProperties(RowGroup.Builder rgb, BlockContentManager bcm, Block g) {
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
				if (pages.isEmpty()) {
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
						currentPage().newRow(r);
					}
				}
				for (RowGroup rg : res.getDiscarded()) {
					addProperties(rg);
				}
				for (RowGroup rg : res.getSupplements()) {
					currentPage().addToPageArea(rg.getRows());
				}
				if (master.getPageArea()!=null && collection!=null && currentPage().pageAreaSpaceNeeded() > master.getPageArea().getMaxHeight()) {
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
			this.map = new HashMap<String, RowGroup>();
		}

		@Override
		public RowGroup get(String id) {
			if (collection!=null) {
				RowGroup ret = map.get(id);
				if (ret==null) {
					RowGroup.Builder b = new RowGroup.Builder(master.getRowSpacing());
					for (Block g : collection.getBlocks(id)) {
						BlockContentManager bcm = g.getBlockContentManager(c);
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
				return (int)Math.floor(advance / getLayoutMaster().getRowSpacing());
			}
		}
		return 0;
	}

}
