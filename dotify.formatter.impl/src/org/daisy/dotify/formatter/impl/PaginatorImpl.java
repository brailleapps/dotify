package org.daisy.dotify.formatter.impl;

import java.io.IOException;

import org.daisy.dotify.api.formatter.CrossReferences;
import org.daisy.dotify.api.formatter.PageStruct;
import org.daisy.dotify.api.translator.BrailleTranslator;
import org.daisy.dotify.tools.StateObject;
import org.daisy.dotify.tools.StringTools;

/**
 * Provides an implementation of the paginator interface. This class should
 * not be used directly, use the corresponding factory methods instead.
 * 
 * @author Joel Håkansson
 */
public class PaginatorImpl {
	private StateObject state;
	private BrailleTranslator translator;
	private Iterable<BlockSequence> fs;
	//private HashMap<String, LayoutMaster> templates;

	public PaginatorImpl() { //HashMap<String, LayoutMaster> templates
		
		this.state = new StateObject();
		//this.templates = templates;
	}
	
	public void open(BrailleTranslator translator, Iterable<BlockSequence> fs) {
		state.assertUnopened();
		this.translator = translator;
		this.fs = fs;
		state.open();
	}

/*
	public LayoutMaster getCurrentLayoutMaster() {
		return currentSequence().getLayoutMaster();
	}*/
	

	// End CurrentPageInfo

	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}
	
	/**
	 * Paginates the supplied block sequence
	 * @param refs the cross references to use
	 * @throws IOException if IO fails
	 */
	public PageStruct paginate(CrossReferences refs) throws PaginatorException {
		PageStructImpl pageStruct = new PageStructImpl();
		for (BlockSequence seq : fs) {
			if (seq.getInitialPageNumber()==null) {
				pageStruct.newSequence(seq.getLayoutMaster(), translator);
			} else {
				pageStruct.newSequence(seq.getLayoutMaster(), seq.getInitialPageNumber() - 1, translator);
			}
			pageStruct.newPage();
			//ArrayList<Block> tmp = new ArrayList<Block>();
			//Block[] groupA = new Block[tmp.size()];
			//groupA = tmp.toArray(groupA);
			//int gi = 0;
			for (Block g : seq) {
				//int height = ps.getCurrentLayoutMaster().getFlowHeight();
				switch (g.getBreakBeforeType()) {
					case PAGE:
						if (pageStruct.spaceUsedInRows(0) > 0) {
							pageStruct.newPage();
						}
						break;
					case AUTO:default:;
				}
				//FIXME: se över recursiv hämtning
				switch (g.getKeepType()) {
					case ALL:
						int keepHeight = seq.getKeepHeight(g, refs);
						if (pageStruct.spaceUsedInRows(0) > 0 && keepHeight > pageStruct.getFlowHeight() - pageStruct.spaceUsedInRows(0) && keepHeight <= pageStruct.getFlowHeight()) {
							pageStruct.newPage();
						}
						break;
					case AUTO:
						break;
					default:;
				}
				if (g.getSpaceBefore()+g.getSpaceAfter()>=pageStruct.getFlowHeight()) {
					throw new PaginatorException("Group margins too large to fit on an empty page.");
				} else if (g.getSpaceBefore() > pageStruct.getFlowHeight() - pageStruct.spaceUsedInRows(1)) {
					pageStruct.currentSequence().newPageOnRow();
				}
				BlockContentManager rdm = g.getBlockContentManager(refs);
				if (g.getVerticalPosition() != null) {
					int blockSpace = rdm.getRowCount() + g.getSpaceBefore() + g.getSpaceAfter();
					int pos = g.getVerticalPosition().getPosition().makeAbsolute(pageStruct.currentPage().getFlowHeight());
					int t = pos - pageStruct.spaceUsedInRows(0);
					if (t > 0) {
						int advance = 0;
						switch (g.getVerticalPosition().getAlignment()) {
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
						for (int i = 0; i < Math.floor(advance / seq.getLayoutMaster().getRowSpacing()); i++) {
							pageStruct.newRow(new RowImpl("", g.getRowDataProperties()));
						}
					}
				}
				if (g.getLeadingDecoration()!=null) {
					pageStruct.newRow(newRow(g.getRowDataProperties(), g.getLeadingDecoration()));
				}
				for (int i=0; i<g.getSpaceBefore();i++) {
					pageStruct.newRow(new RowImpl("", g.getRowDataProperties()));
				}

				pageStruct.insertMarkers(rdm.getGroupMarkers());
				boolean first = true;
				
				if (rdm.getRowCount()==0 && !"".equals(g.getIdentifier())) {
					pageStruct.insertIdentifier(g.getIdentifier());
				}
				pageStruct.currentSequence().setKeepWithNextSheets(g.getKeepWithNextSheets());
				for (RowImpl row : rdm) {
					if (first) {
						first = false;
						if (!"".equals(g.getIdentifier())) {
							pageStruct.newRow(row, g.getIdentifier());
						} else {
							pageStruct.newRow(row);
						}
					} else {
						pageStruct.newRow(row);
					}
				}
				pageStruct.currentSequence().setKeepWithPreviousSheets(g.getKeepWithPreviousSheets());
				if (g.getSpaceAfter() > pageStruct.getFlowHeight() - pageStruct.spaceUsedInRows(1)) {
					pageStruct.currentSequence().newPageOnRow();
				} else {
					for (int i=0; i<g.getSpaceAfter();i++) {
						pageStruct.newRow(new RowImpl("", g.getRowDataProperties()));
					}
				}
				if (g.getTrailingDecoration()!=null) {
					pageStruct.newRow(newRow(g.getRowDataProperties(), g.getTrailingDecoration()));
				}
				//gi++;
			}
		}
		return pageStruct;
	}
	
	private RowImpl newRow(RowDataProperties rdp, SingleLineDecoration d) {
		int w = rdp.getMaster().getFlowWidth() - rdp.getRightMarginParent().getContent().length() - rdp.getLeftMarginParent().getContent().length();
		int aw = w-d.getLeftCorner().length()-d.getRightCorner().length();
		RowImpl row = new RowImpl(d.getLeftCorner() + StringTools.fill(d.getLinePattern(), aw) + d.getRightCorner());
		row.setLeftMargin(rdp.getLeftMarginParent());
		row.setRightMargin(rdp.getRightMarginParent());
		return row;
	}

}
