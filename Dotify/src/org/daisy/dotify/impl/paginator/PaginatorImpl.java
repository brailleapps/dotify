package org.daisy.dotify.impl.paginator;

import java.io.IOException;

import org.daisy.dotify.formatter.Block;
import org.daisy.dotify.formatter.BlockSequence;
import org.daisy.dotify.formatter.CrossReferences;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.Row;
import org.daisy.dotify.formatter.BlockContentManager;
import org.daisy.dotify.paginator.PageStruct;
import org.daisy.dotify.paginator.Paginator;
import org.daisy.dotify.paginator.PaginatorException;
import org.daisy.dotify.tools.StateObject;

/**
 * Provides an implementation of the paginator interface. This class should
 * not be used directly, use the corresponding factory methods instead.
 * 
 * @author Joel Håkansson
 */
public class PaginatorImpl implements Paginator {
	private StateObject state;
	private FormatterFactory formatterFactory;
	private Iterable<BlockSequence> fs;
	//private HashMap<String, LayoutMaster> templates;

	public PaginatorImpl() { //HashMap<String, LayoutMaster> templates
		
		this.state = new StateObject();
		//this.templates = templates;
	}
	
	public void open(FormatterFactory formatterFactory, Iterable<BlockSequence> fs) {
		state.assertUnopened();
		this.formatterFactory = formatterFactory;
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
				pageStruct.newSequence(seq.getLayoutMaster(), formatterFactory);
			} else {
				pageStruct.newSequence(seq.getLayoutMaster(), seq.getInitialPageNumber()-1, formatterFactory);
			}
			pageStruct.newPage();
			//ArrayList<Block> tmp = new ArrayList<Block>();
			//Block[] groupA = new Block[tmp.size()];
			//groupA = tmp.toArray(groupA);
			int gi = 0;
			for (Block g : seq) {
				//int height = ps.getCurrentLayoutMaster().getFlowHeight();
				switch (g.getBreakBeforeType()) {
					case PAGE:
						if (pageStruct.countRows()>0) {
							pageStruct.newPage();
						}
						break;
					case AUTO:default:;
				}
				//FIXME: se över recursiv hämtning
				switch (g.getKeepType()) {
					case ALL:
						int keepHeight = getKeepHeight(seq, gi, refs);
						if (pageStruct.countRows()>0 && keepHeight>pageStruct.getFlowHeight()-pageStruct.countRows() && keepHeight<=pageStruct.getFlowHeight()) {
							pageStruct.newPage();
						}
						break;
					case AUTO:
						break;
					default:;
				}
				if (g.getSpaceBefore()+g.getSpaceAfter()>=pageStruct.getFlowHeight()) {
					throw new PaginatorException("Group margins too large to fit on an empty page.");
				} else if (g.getSpaceBefore()+1>pageStruct.getFlowHeight()-pageStruct.countRows()) {
					pageStruct.currentSequence().newPageOnRow();
				}
				for (int i=0; i<g.getSpaceBefore();i++) {
					pageStruct.newRow(new Row(""));
				}
				BlockContentManager rdm = g.getBlockContentManager(refs);
				pageStruct.insertMarkers(rdm.getGroupMarkers());
				boolean first = true;
				
				if (rdm.getRowCount()==0 && !"".equals(g.getIdentifier())) {
					pageStruct.insertIdentifier(g.getIdentifier());
				}
				pageStruct.currentSequence().setKeepWithNextSheets(g.getKeepWithNextSheets());
				for (Row row : rdm) {
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
				if (g.getSpaceAfter()>=pageStruct.getFlowHeight()-pageStruct.countRows()) {
					pageStruct.currentSequence().newPageOnRow();
				} else {
					for (int i=0; i<g.getSpaceAfter();i++) {
						pageStruct.newRow(new Row(""));
					}
				}
				gi++;
			}
		}
		return pageStruct;
	}
	
	private static int getKeepHeight(BlockSequence seq, int gi, CrossReferences refs) {
		int keepHeight = seq.getBlock(gi).getSpaceBefore()+seq.getBlock(gi).getBlockContentManager(refs).getRowCount();
		if (seq.getBlock(gi).getKeepWithNext()>0 && gi+1<seq.getBlockCount()) {
			keepHeight += seq.getBlock(gi).getSpaceAfter()+seq.getBlock(gi+1).getSpaceBefore()+seq.getBlock(gi).getKeepWithNext();
			switch (seq.getBlock(gi+1).getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(seq, gi+1, refs);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}

}
