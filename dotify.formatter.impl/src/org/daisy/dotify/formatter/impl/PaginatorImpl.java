package org.daisy.dotify.formatter.impl;

import java.io.IOException;

/**
 * Provides an implementation of the paginator interface. This class should
 * not be used directly, use the corresponding factory methods instead.
 * 
 * @author Joel Håkansson
 */
public class PaginatorImpl {
	private final FormatterContext context;
	private final Iterable<BlockSequence> fs;

	public PaginatorImpl(FormatterContext context, Iterable<BlockSequence> fs) {
		this.context = context;
		this.fs = fs;
	}
	
	/**
	 * Paginates the supplied block sequence
	 * @param refs the cross references to use
	 * @throws IOException if IO fails
	 */
	public PageStructImpl paginate(CrossReferences refs, DefaultContext rcontext) throws PaginatorException {
		PageStructImpl pageStruct = new PageStructImpl(context);
		for (BlockSequence seq : fs) {
			if (seq.getInitialPageNumber()==null) {
				pageStruct.newSequence(seq.getLayoutMaster());
			} else {
				pageStruct.newSequence(seq.getLayoutMaster(), seq.getInitialPageNumber() - 1);
			}
			pageStruct.newPage();

			//update context
			for (Block g : seq) {
				g.setContext(seq.getLayoutMaster().getFlowWidth(), refs, rcontext, context);
			}

			//layout
			for (Block g : seq) {
				BlockDataContext bd = new BlockDataContext(g);
				//FIXME: this assumes that row spacing is equal to 1
				if (bd.rdm.countPreContentRows()+bd.rdm.countPostContentRows()>=pageStruct.getFlowHeight()) {
					throw new PaginatorException("Group margins too large to fit on an empty page.");
				}
				
				//Start new page if needed
				bd.startNewPageIfNeeded(pageStruct, seq);
				bd.addVerticalSpace(pageStruct);
				
				addRows(bd.rdm.getPreContentRows(), pageStruct);
				
				pageStruct.insertMarkers(bd.rdm.getGroupMarkers());
				if (bd.rdm.getRowCount()==0 && !"".equals(g.getIdentifier())) {
					pageStruct.insertIdentifier(g.getIdentifier());
				}

				pageStruct.currentSequence().setKeepWithNextSheets(g.getKeepWithNextSheets());
				bd.addRows(pageStruct);
				pageStruct.currentSequence().setKeepWithPreviousSheets(g.getKeepWithPreviousSheets());

				addRows(bd.rdm.getPostContentRows(), pageStruct);
				
				//FIXME: this assumes that row spacing is equal to 1
				if (bd.rdm.countSkippablePostContentRows() > pageStruct.getFlowHeight() - pageStruct.spaceUsedInRows(1)) {
					pageStruct.currentSequence().newPageOnRow();
				} else {
					addRows(bd.rdm.getSkippablePostContentRows(), pageStruct);
				}
				//gi++;
			}
		}
		return pageStruct;
	}
	
	private void addRows(Iterable<RowImpl> rows, PageStructImpl pageStruct) {
		for (RowImpl r : rows) {
			pageStruct.newRow(r);
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
		
		private void addVerticalSpace(PageStructImpl pageStruct) {
			if (block.getVerticalPosition() != null) {
				int blockSpace = rdm.getRowCount() + block.getSpaceBefore() + block.getSpaceAfter();
				int pos = block.getVerticalPosition().getPosition().makeAbsolute(pageStruct.currentPage().getFlowHeight());
				int t = pos - pageStruct.spaceUsedInRows(0);
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
					for (int i = 0; i < Math.floor(advance / pageStruct.currentSequence().getLayoutMaster().getRowSpacing()); i++) {
						pageStruct.newRow(new RowImpl("", rdm.getLeftMarginParent(), rdm.getRightMarginParent()));
					}
				}
			}
		}
		
		private void startNewPageIfNeeded(PageStructImpl pageStruct, BlockSequence seq) {
			boolean hasContent = pageStruct.spaceUsedInRows(0) > 0;
			switch (block.getBreakBeforeType()) {
				case PAGE:
					if (hasContent) {
						pageStruct.newPage();
					}
					break;
				case AUTO:default:;
			}
			switch (block.getKeepType()) {
				case ALL:
					int keepHeight = seq.getKeepHeight(block);
					//FIXME: this assumes that row spacing is equal to 1
					if (hasContent && keepHeight > pageStruct.getFlowHeight() - pageStruct.spaceUsedInRows(0) && keepHeight <= pageStruct.getFlowHeight()) {
						pageStruct.newPage();
					}
					break;
				case AUTO:
					break;
				default:;
			}
			//FIXME: this assumes that row spacing is equal to 1
			if (block.getSpaceBefore() > pageStruct.getFlowHeight() - pageStruct.spaceUsedInRows(1)) {
				pageStruct.currentSequence().newPageOnRow();
			}
		}


		private void addRows(PageStructImpl pageStruct) {
			boolean first = true;
			for (RowImpl row : rdm) {
				pageStruct.newRow(row);
				if (first) {
					first = false;
					if (!"".equals(block.getIdentifier())) {
						pageStruct.insertIdentifier(block.getIdentifier());
					}
				}
			}
		}
	}

}
