package org.daisy.dotify.impl.paginator;

import java.io.IOException;
import java.util.List;

import org.daisy.dotify.formatter.FormatterException;
import org.daisy.dotify.formatter.FormatterFactory;
import org.daisy.dotify.formatter.Paginator;
import org.daisy.dotify.formatter.dom.CrossReferences;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.formatter.dom.Marker;
import org.daisy.dotify.formatter.dom.Page;
import org.daisy.dotify.formatter.dom.PageInfo;
import org.daisy.dotify.formatter.dom.PageSequence;
import org.daisy.dotify.formatter.dom.PageStruct;
import org.daisy.dotify.formatter.dom.Row;
import org.daisy.dotify.formatter.dom.block.Block;
import org.daisy.dotify.formatter.dom.block.BlockSequence;
import org.daisy.dotify.formatter.dom.block.RowDataManager;
import org.daisy.dotify.tools.StateObject;


public class PaginatorImpl implements Paginator, PageInfo {
	private PageStructImpl pageStruct;
	private StateObject state;
	private FormatterFactory formatterFactory;
	//private HashMap<String, LayoutMaster> templates;

	public PaginatorImpl() { //HashMap<String, LayoutMaster> templates
		
		this.state = new StateObject();
		//this.templates = templates;
	}
	
	public void open(FormatterFactory formatterFactory) {
		state.assertUnopened();
		this.formatterFactory = formatterFactory;
	}

	private void newSequence(LayoutMaster master, int pagesOffset) {
		state.assertOpen();
		//sequence.push(new PageSequence(templates.get(masterName)));
		pageStruct.push(new PageSequenceImpl(master, pagesOffset, pageStruct.pageReferences, formatterFactory));
	}
	
	private void newSequence(LayoutMaster master) {
		if (pageStruct.size()==0) {
			newSequence(master, 0);
		} else {
			int next = ((PageSequenceImpl)pageStruct.peek()).currentPage().getPageIndex()+1;
			if (pageStruct.peek().getLayoutMaster().duplex() && (next % 2)==1) {
				next++;
			}
			newSequence(master, next);
		}
	}
	
	private PageSequence currentSequence() {
		return pageStruct.peek();
	}

	private Page currentPage() {
		return ((PageSequenceImpl)currentSequence()).currentPage();
	}

	private void newPage() {
		state.assertOpen();
		((PageSequenceImpl)currentSequence()).newPage();
	}
	
	private void newRow(Row row) {
		state.assertOpen();
		((PageSequenceImpl)currentSequence()).newRow(row);
	}
	
	private void newRow(Row row, String id) {
		state.assertOpen();
		((PageSequenceImpl)currentSequence()).newRow(row, id);
	}

	private void insertMarkers(List<Marker> m) {
		state.assertOpen();
		((PageImpl)((PageSequenceImpl)currentSequence()).currentPage()).addMarkers(m);
	}
/*
	public LayoutMaster getCurrentLayoutMaster() {
		return currentSequence().getLayoutMaster();
	}*/
	
	private PageInfo getPageInfo() {
		return this;
	}
	
	// CurrentPageInfo
	public int countRows() {
		state.assertOpen();
		//return currentSequence().rowsOnCurrentPage();
		return ((PageImpl)currentPage()).rowsOnPage();
	}
	
	public int getFlowHeight() {
		state.assertOpen();
		return ((PageImpl)currentPage()).getFlowHeight();
	}
	// End CurrentPageInfo

	public PageStruct getPageStruct() {
		state.assertClosed();
		return pageStruct;
	}
	
	public void close() throws IOException {
		if (state.isClosed()) {
			return;
		}
		state.assertOpen();
		state.close();
	}

	private void insertIdentifier(String id) {
		((PageSequenceImpl)currentSequence()).insertIdentifier(id);
	}
	
	/**
	 * Paginates the supplied block sequence
	 * @param fs the block sequence interable
	 * @param refs the cross references to use
	 * @throws IOException if IO fails
	 */
	public void paginate(Iterable<BlockSequence> fs, CrossReferences refs) throws IOException {
		state.assertNotOpen();
		this.pageStruct = new PageStructImpl();
		state.open();
		for (BlockSequence seq : fs) {
			if (seq.getInitialPageNumber()==null) {
				newSequence(seq.getLayoutMaster());
			} else {
				newSequence(seq.getLayoutMaster(), seq.getInitialPageNumber()-1);
			}
			newPage();
			//ArrayList<Block> tmp = new ArrayList<Block>();
			//Block[] groupA = new Block[tmp.size()];
			//groupA = tmp.toArray(groupA);
			int gi = 0;
			for (Block g : seq) {
				//int height = ps.getCurrentLayoutMaster().getFlowHeight();
				switch (g.getBreakBeforeType()) {
					case PAGE:
						if (getPageInfo().countRows()>0) {
							newPage();
						}
						break;
					case AUTO:default:;
				}
				//FIXME: se över recursiv hämtning
				switch (g.getKeepType()) {
					case ALL:
						int keepHeight = getKeepHeight(seq, gi, refs);
						if (getPageInfo().countRows()>0 && keepHeight>getPageInfo().getFlowHeight()-getPageInfo().countRows() && keepHeight<=getPageInfo().getFlowHeight()) {
							newPage();
						}
						break;
					case AUTO:
						break;
					default:;
				}
				if (g.getSpaceBefore()+g.getSpaceAfter()>=getPageInfo().getFlowHeight()) {
					IOException ex = new IOException("Layout exception");
					ex.initCause(new FormatterException("Group margins too large to fit on an empty page."));
					throw ex;
				} else if (g.getSpaceBefore()+1>getPageInfo().getFlowHeight()-getPageInfo().countRows()) {
					newPage();
				}
				for (int i=0; i<g.getSpaceBefore();i++) {
					newRow(new Row(""));
				}
				RowDataManager rdm = g.getRowDataManager(refs);
				insertMarkers(rdm.getGroupMarkers());
				boolean first = true;
				
				if (rdm.getRowCount()==0 && !"".equals(g.getIdentifier())) {
					insertIdentifier(g.getIdentifier());
				}
				((PageSequenceImpl)currentSequence()).setKeepWithNextSheets(g.getKeepWithNextSheets());
				for (Row row : rdm) {
					if (first) {
						first = false;
						if (!"".equals(g.getIdentifier())) {
							newRow(row, g.getIdentifier());
						} else {
							newRow(row);
						}
					} else {
						newRow(row);
					}
				}
				((PageSequenceImpl)currentSequence()).setKeepWithPreviousSheets(g.getKeepWithPreviousSheets());
				if (g.getSpaceAfter()>=getPageInfo().getFlowHeight()-getPageInfo().countRows()) {
					newPage();
				} else {
					for (int i=0; i<g.getSpaceAfter();i++) {
						newRow(new Row(""));
					}
				}
				gi++;
			}
		}
		state.close();
	}
	
	private static int getKeepHeight(BlockSequence seq, int gi, CrossReferences refs) {
		int keepHeight = seq.getBlock(gi).getSpaceBefore()+seq.getBlock(gi).getRowDataManager(refs).getRowCount();
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
