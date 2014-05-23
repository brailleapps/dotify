package org.daisy.dotify.formatter.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.daisy.dotify.api.formatter.PageAreaProperties;

/**
 * Provides an implementation of the paginator interface. This class should
 * not be used directly, use the corresponding factory methods instead.
 * 
 * @author Joel Håkansson
 */
public class PaginatorImpl {
	private final FormatterContext context;
	private final Iterable<BlockSequence> fs;
	private final HashMap<String, ContentCollectionImpl> collections;

	public PaginatorImpl(FormatterContext context, Iterable<BlockSequence> fs, HashMap<String, ContentCollectionImpl> collections) {
		this.context = context;
		this.fs = fs;
		this.collections = collections;
	}
	
	/**
	 * Paginates the supplied block sequence
	 * @param refs the cross references to use
	 * @throws IOException if IO fails
	 */
	public PageStructBuilder paginate(CrossReferences refs, DefaultContext rcontext) throws PaginatorException {
	restart:while (true) {
		PageStructBuilder pageStruct = new PageStructBuilder(context);
		PageSequenceBuilder pageSeq = null;
		for (BlockSequence seq : fs) {
			ContentCollectionImpl c = null;
			PageAreaProperties pa = seq.getLayoutMaster().getPageArea();
			if (pa!=null) {
				c = collections.get(pa.getCollectionId());
			}
			List<RowImpl> before = new ArrayList<RowImpl>();
			List<RowImpl> after = new ArrayList<RowImpl>();
			PageAreaBuilderImpl pab = seq.getLayoutMaster().getPageAreaBuilder();
			if (pab !=null) {
				//Assumes before is static
				for (Block b : pab.getBeforeArea()) {
					b.setContext(seq.getLayoutMaster().getFlowWidth(), refs, rcontext, context);
					for (RowImpl r : b.getBlockContentManager()) {
						before.add(r);
					}
				}
				
				//Assumes after is static
				for (Block b : pab.getAfterArea()) {
					b.setContext(seq.getLayoutMaster().getFlowWidth(), refs, rcontext, context);
					for (RowImpl r : b.getBlockContentManager()) {
						after.add(r);
					}
				}
			}
			if (seq.getInitialPageNumber()==null) {
				pageSeq = pageStruct.newSequence(seq.getLayoutMaster(), before, after);
			} else {
				pageSeq = pageStruct.newSequence(seq.getLayoutMaster(), seq.getInitialPageNumber() - 1, before, after);
			}
			pageSeq.newPage();

			//update context
			for (Block g : seq) {
				g.setContext(seq.getLayoutMaster().getFlowWidth(), refs, rcontext, context);
			}

			//layout
			for (Block g : seq) {
				BlockDataContext bd = new BlockDataContext(g);
				//FIXME: this assumes that row spacing is equal to 1
				if (bd.rdm.countPreContentRows()+bd.rdm.countPostContentRows()>=pageSeq.currentPage().getFlowHeight()) {
					throw new PaginatorException("Group margins too large to fit on an empty page.");
				}
				
				//Start new page if needed
				bd.startNewPageIfNeeded(pageSeq, seq);
				bd.addVerticalSpace(pageSeq);
				
				addRows(bd.rdm.getPreContentRows(), pageSeq);
				
				pageSeq.currentPage().addMarkers(bd.rdm.getGroupMarkers());
				if (bd.rdm.getRowCount()==0 && !"".equals(g.getIdentifier())) {
					pageSeq.insertIdentifier(g.getIdentifier());
				}

				pageSeq.setKeepWithNextSheets(g.getKeepWithNextSheets());
				if (!bd.addRows(pageSeq, seq.getLayoutMaster(), refs, rcontext, c)) {
					//reassign collection
					if (pa!=null) {
						c = collections.remove(pa.getCollectionId());
						if (collections.put(pa.getFallbackId(), c)!=null) {
							throw new PaginatorException("Fallback id already in use:" + pa.getFallbackId());
						}
					}
					//restart formatting
					continue restart;
				}
				pageSeq.setKeepWithPreviousSheets(g.getKeepWithPreviousSheets());

				addRows(bd.rdm.getPostContentRows(), pageSeq);
				
				//FIXME: this assumes that row spacing is equal to 1
				if (bd.rdm.countSkippablePostContentRows() > pageSeq.currentPage().getFlowHeight() - pageSeq.spaceUsedOnPage(1)) {
					pageSeq.newPageOnRow();
				} else {
					addRows(bd.rdm.getSkippablePostContentRows(), pageSeq);
				}
				//gi++;
			}
		}
		return pageStruct;
	}
	}
	
	private void addRows(Iterable<RowImpl> rows, PageSequenceBuilder pageSeq) {
		for (RowImpl r : rows) {
			pageSeq.newRow(r);
		}
	}
	
	/**
	 * Provides the block in the context of the supplied parameters.
	 */
	private class BlockDataContext {
		private final Block block;
		private final BlockContentManager rdm;
		
		private BlockDataContext(Block block) {
			this.block = block;
			this.rdm = block.getBlockContentManager();
		}
		
		private void addVerticalSpace(PageSequenceBuilder pageSeq) {
			if (block.getVerticalPosition() != null) {			
				int blockSpace = rdm.getRowCount() + block.getSpaceBefore() + block.getSpaceAfter();
				int pos = block.getVerticalPosition().getPosition().makeAbsolute(pageSeq.currentPage().getFlowHeight());
				int t = pos - pageSeq.spaceUsedOnPage(0);
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
					for (int i = 0; i < Math.floor(advance / pageSeq.getLayoutMaster().getRowSpacing()); i++) {
						pageSeq.newRow(new RowImpl("", rdm.getLeftMarginParent(), rdm.getRightMarginParent()));
					}
				}
			}
		}
		
		private void startNewPageIfNeeded(PageSequenceBuilder pageSeq, BlockSequence seq) {
			boolean hasContent = pageSeq.spaceUsedOnPage(0) > 0;
			switch (block.getBreakBeforeType()) {
				case PAGE:
					if (hasContent) {
						pageSeq.newPage();
					}
					break;
				case AUTO:default:;
			}
			switch (block.getKeepType()) {
				case ALL:
					int keepHeight = seq.getKeepHeight(block);
					//FIXME: this assumes that row spacing is equal to 1
					if (hasContent && keepHeight > pageSeq.currentPage().getFlowHeight() - pageSeq.spaceUsedOnPage(0) && keepHeight <= pageSeq.currentPage().getFlowHeight()) {
						pageSeq.newPage();
					}
					break;
				case AUTO:
					break;
				default:;
			}
			//FIXME: this assumes that row spacing is equal to 1
			if (block.getSpaceBefore() > pageSeq.currentPage().getFlowHeight() - pageSeq.spaceUsedOnPage(1)) {
				pageSeq.newPageOnRow();
			}
		}


		private boolean addRows(PageSequenceBuilder pageSeq, LayoutMaster master, CrossReferences refs, DefaultContext rcontext, ContentCollectionImpl c) {
			boolean first = true;
			for (RowImpl row : rdm) {
				if (master.getPageArea()!=null && c!=null) {
					ArrayList<RowImpl> blk = new ArrayList<RowImpl>();
					for (String a : row.getAnchors()) {
						for (Block b : c.getBlocks(a)) {
							b.setContext(master.getFlowWidth(), refs, rcontext, context);
						}
						for (Block b : c.getBlocks(a)) {
							for (RowImpl r : b.getBlockContentManager().getPreContentRows()) {
								blk.add(r);
							}
							for (RowImpl r : b.getBlockContentManager()) {
								blk.add(r);
							}
							for (RowImpl r : b.getBlockContentManager().getPostContentRows()) {
								blk.add(r);
							}
							for (RowImpl r : b.getBlockContentManager().getSkippablePostContentRows()) {
								blk.add(r);
							}
						}
					}
					if (blk.size()>0) {
						pageSeq.newRow(row, blk);
						//The text volume is reduced if row spacing increased
						if (pageSeq.currentPage().pageAreaSpaceNeeded() > master.getPageArea().getMaxHeight()) {
							return false;
						}
					} else {
						pageSeq.newRow(row);
					}
				} else {
					pageSeq.newRow(row);
				}
				if (first) {
					first = false;
					if (!"".equals(block.getIdentifier())) {
						pageSeq.insertIdentifier(block.getIdentifier());
					}
				}
			}
			return true;
		}
	}

}
