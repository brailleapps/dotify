package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.daisy.dotify.api.formatter.FallbackRule;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.api.formatter.PageAreaProperties;
import org.daisy.dotify.api.formatter.RenameFallbackRule;

class PageSequenceBuilder extends PageSequence {
	private final Map<String, PageImpl> pageReferences;
	private final FormatterContext context;
	private final BlockSequence seq;
	private final CrossReferenceHandler crh;
	private int keepNextSheets;
	private PageImpl nextPage;
	private PageAreaContent staticAreaContent;
	private ContentCollectionImpl collection;
	private PageAreaProperties areaProps;

	PageSequenceBuilder(CrossReferenceHandler crh, BlockSequence seq, Map<String, PageImpl> pageReferences, FormatterContext context) {
		super(seq.getLayoutMaster());
		this.pageReferences = pageReferences;
		this.context = context;
		this.seq = seq;
		this.crh = crh;
		reset();
	}
	
	private void reset() {
		this.keepNextSheets = 0;
		this.nextPage = null;

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

	private void newPageOnRow() {
		if (nextPage!=null) {
			//if new page is already in buffer, flush it.
			newPage();
		}
		nextPage = new PageImpl(master, context, this, pages.size()+pagesOffset, staticAreaContent.getBefore(), staticAreaContent.getAfter());
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

	private void newRow(RowImpl row, List<RowImpl> block) {
		if (nextPage != null || currentPage().spaceNeeded(block) + currentPage().spaceNeeded() + 1 > currentPage().getFlowHeight()) {
			newPage();
		}
		currentPage().newRow(row);
		currentPage().addToPageArea(block);
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
	
	private void initPagination(int pagesOffset) {
		if (seq.getInitialPageNumber()!=null) {
			this.pagesOffset = seq.getInitialPageNumber() - 1;
		} else {
			this.pagesOffset = pagesOffset;
		}

		collection = null;
		areaProps = seq.getLayoutMaster().getPageArea();
		if (areaProps!=null) {
			collection = context.getCollections().get(areaProps.getCollectionId());
		}
	}

	boolean paginate(int pagesOffset, CrossReferences refs, DefaultContext rcontext) throws PaginatorException  {
		initPagination(pagesOffset);
		
		BlockContext blockContext = new BlockContext(seq.getLayoutMaster().getFlowWidth(), refs, rcontext, context);
		staticAreaContent = new PageAreaContent(seq.getLayoutMaster().getPageAreaBuilder(), blockContext);
		
		newPage();

		//layout
		MarginValue max = new MarginValue();
		int currentPageNumber = -1;
		for (int x=0; x<seq.size(); x++) {
			Block g = seq.get(x);
			BlockDataContext bd = new BlockDataContext(g, blockContext);
			//Start new page if needed
			bd.startNewPageIfNeeded(seq);
			//if we are on a new page, then the collapsing regions are irrelevant
			if (currentPageNumber() > currentPageNumber) {
				max = new MarginValue(); 
				currentPageNumber = currentPageNumber();
			}
			if (g.getBlockContentManager(blockContext).isCollapsable()
					//This is a hack in order to avoid regression.
					//It retains empty rows at the end of pages in certain cases.
					//Once collapsing borders have been fully tested, this can be removed
					&& (x==seq.size()-1 || seq.get(x+1).getBreakBeforeType()!=FormattingTypes.BreakBefore.PAGE)
					) {
				max = max(max, new MarginValue(g.getRowDataProperties().getRowSpacing(), Math.max(g.getRowDataProperties().getOuterSpaceBefore(), g.getRowDataProperties().getOuterSpaceAfter())));
				
				currentPage().addMarkers(bd.rdm.getGroupMarkers());
				if (bd.rdm.getRowCount()==0 && !"".equals(g.getIdentifier())) {
					insertIdentifier(g.getIdentifier());
				}
				setKeepWithNextSheets(g.getKeepWithNextSheets());
				setKeepWithPreviousSheets(g.getKeepWithPreviousSheets());
				continue;
			}
			max = max(max, new MarginValue(g.getRowDataProperties().getRowSpacing(), g.getRowDataProperties().getOuterSpaceBefore()));
			List<RowImpl> preContentRows = bd.rdm.getPreContentRows(max.rows, max.rowSpacing);
			//FIXME: this assumes that row spacing is equal to 1
			if (preContentRows.size()+bd.rdm.countPostContentRows()>=currentPage().getFlowHeight()) {
				throw new PaginatorException("Group margins too large to fit on an empty page.");
			}

			bd.addVerticalSpace();

			addRows(preContentRows);

			currentPage().addMarkers(bd.rdm.getGroupMarkers());
			if (bd.rdm.getRowCount()==0 && !"".equals(g.getIdentifier())) {
				insertIdentifier(g.getIdentifier());
			}

			setKeepWithNextSheets(g.getKeepWithNextSheets());
			//add content
			if (!bd.addRows()) {
				reassignCollection();
				//restart formatting
				return false;
			}
			setKeepWithPreviousSheets(g.getKeepWithPreviousSheets());

			addRows(bd.rdm.getPostContentRows());

			//FIXME: this assumes that row spacing is equal to 1
			if (bd.rdm.countSkippablePostContentRows() > currentPage().getFlowHeight() - spaceUsedOnPage(1)) {
				newPageOnRow();
			} else {
				addRows(bd.rdm.getSkippablePostContentRows());
			}
			//gi++;
			max = new MarginValue();
		}
		return true;
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
	
	private class MarginValue {
		private final Float rowSpacing;
		private final int rows;
		MarginValue() {
			this(master.getRowSpacing(), 0);
		}
		MarginValue(Float rowSpacing, int rows) {
			this.rowSpacing = rowSpacing;
			this.rows = rows;
		}
		
		private float getHeight() {
			if (rowSpacing==null) {
				return master.getRowSpacing()*rows;
			} else {
				return rowSpacing*rows;
			}
		}
	}
	
	private MarginValue max(MarginValue val1, MarginValue val2) {
		return (val1.getHeight()>val2.getHeight()?val1:val2);
	}
	
	private void addRows(Iterable<RowImpl> rows) {
		for (RowImpl r : rows) {
			newRow(r);
		}
	}

	/**
	 * Provides the block in the context of the supplied parameters.
	 */
	private class BlockDataContext {
		private final Block block;
		private final BlockContext bc;
		private final BlockContentManager rdm;

		private BlockDataContext(Block block, BlockContext bc) {
			this.block = block;
			this.bc = bc;
			this.rdm = block.getBlockContentManager(bc);
		}

		private void addVerticalSpace() {
			if (block.getVerticalPosition() != null) {
				int blockSpace = block.getBlockContentManager(bc).getBlockHeight();
				int pos = block.getVerticalPosition().getPosition().makeAbsolute(currentPage().getFlowHeight());
				int t = pos - spaceUsedOnPage(0);
				if (t > 0) {
					int advance = 0;
					switch (block.getVerticalPosition().getAlignment()) {
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
					for (int i = 0; i < Math.floor(advance / getLayoutMaster().getRowSpacing()); i++) {
						newRow(new RowImpl("", rdm.getLeftMarginParent(), rdm.getRightMarginParent()));
					}
				}
			}
		}

		private void startNewPageIfNeeded(BlockSequence seq) {
			boolean hasContent = spaceUsedOnPage(0) > 0;
			switch (block.getBreakBeforeType()) {
			case PAGE:
				if (hasContent) {
					newPage();
				}
				break;
			case AUTO:default:;
			}
			switch (block.getKeepType()) {
			case ALL:
				int keepHeight = seq.getKeepHeight(block, bc);
				//FIXME: this assumes that row spacing is equal to 1
				if (hasContent && keepHeight > currentPage().getFlowHeight() - spaceUsedOnPage(0) && keepHeight <= currentPage().getFlowHeight()) {
					newPage();
				}
				break;
			case AUTO:
				break;
			default:;
			}
			//FIXME: this assumes that row spacing is equal to 1
			if (block.getRowDataProperties().getOuterSpaceBefore() + block.getRowDataProperties().getInnerSpaceBefore() > currentPage().getFlowHeight() - spaceUsedOnPage(1)) {
				newPageOnRow();
			}
		}

		private boolean addRows() {
			boolean first = true;
			for (RowImpl row : rdm) {
				if (master.getPageArea()!=null && collection!=null) {
					List<RowImpl> blk = getSupplements(row);
					if (!blk.isEmpty()) {
						newRow(row, blk);
						//The text volume is reduced if row spacing increased
						if (currentPage().pageAreaSpaceNeeded() > master.getPageArea().getMaxHeight()) {
							return false;
						}
					} else {
						newRow(row);
					}
				} else {
					newRow(row);
				}
				if (first) {
					first = false;
					if (!"".equals(block.getIdentifier())) {
						insertIdentifier(block.getIdentifier());
					}
				}
			}
			return true;
		}
		
		private List<RowImpl> getSupplements(RowImpl row) {
			List<RowImpl> blk = new ArrayList<RowImpl>();
			for (String a : row.getAnchors()) {
				if (!currentPage().getAnchors().contains(a)) {
					//page doesn't already contains these blocks
					for (Block b : collection.getBlocks(a)) {
						for (RowImpl r : b.getBlockContentManager(bc).getPreContentRows(b.getRowDataProperties().getOuterSpaceBefore(), b.getRowDataProperties().getRowSpacing())) {
							blk.add(r);
						}
						for (RowImpl r : b.getBlockContentManager(bc)) {
							blk.add(r);
						}
						for (RowImpl r : b.getBlockContentManager(bc).getPostContentRows()) {
							blk.add(r);
						}
						for (RowImpl r : b.getBlockContentManager(bc).getSkippablePostContentRows()) {
							blk.add(r);
						}
					}
				}
			}
			return blk;
		}
	}
}
