package org.daisy.dotify.formatter;

import java.io.IOException;


/**
 * Provides breaking the block structure into pages, given the rules in
 * the block structure and the layout templates.
 * @author Joel Håkansson
 */
public class PaginatorHandler {

	/**
	 * Paginates the supplied FlowStruct using the supplied Paginator
	 * @param paginator the paginator to use
	 * @throws IOException if IO fails
	 */
	public static void paginate(Iterable<BlockSequence> fs, Paginator paginator) throws IOException {
		for (BlockSequence seq : fs) {
			if (seq.getInitialPageNumber()==null) {
				paginator.newSequence(seq.getLayoutMaster());
			} else {
				paginator.newSequence(seq.getLayoutMaster(), seq.getInitialPageNumber()-1);
			}
			paginator.newPage();
			//ArrayList<Block> tmp = new ArrayList<Block>();
			//Block[] groupA = new Block[tmp.size()];
			//groupA = tmp.toArray(groupA);
			int gi = 0;
			for (Block g : seq) {
				//int height = ps.getCurrentLayoutMaster().getFlowHeight();
				switch (g.getBreakBeforeType()) {
					case PAGE:
						if (paginator.getPageInfo().countRows()>0) {
							paginator.newPage();
						}
						break;
					case AUTO:default:;
				}
				//FIXME: se över recursiv hämtning
				switch (g.getKeepType()) {
					case ALL:
						int keepHeight = getKeepHeight(seq, gi);
						if (paginator.getPageInfo().countRows()>0 && keepHeight>paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows() && keepHeight<=paginator.getPageInfo().getFlowHeight()) {
							paginator.newPage();
						}
						break;
					case AUTO:
						break;
					default:;
				}
				if (g.getSpaceBefore()+g.getSpaceAfter()>=paginator.getPageInfo().getFlowHeight()) {
					IOException ex = new IOException("Layout exception");
					ex.initCause(new LayoutException("Group margins too large to fit on an empty page."));
					throw ex;
				} else if (g.getSpaceBefore()+1>paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows()) {
					paginator.newPage();
				}
				for (int i=0; i<g.getSpaceBefore();i++) {
					paginator.newRow(new Row(""));
				}
				paginator.insertMarkers(g.getGroupMarkers());
				boolean first = true;
				if (g.getRowCount()==0 && !"".equals(g.getIdentifier())) {
					paginator.insertIdentifier(g.getIdentifier());
				}
				for (Row row : g) {
					if (first) {
						first = false;
						if (!"".equals(g.getIdentifier())) {
							paginator.newRow(row, g.getIdentifier());
						} else {
							paginator.newRow(row);
						}
					} else {
						paginator.newRow(row);
					}
				}
				if (g.getSpaceAfter()>=paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows()) {
					paginator.newPage();
				} else {
					for (int i=0; i<g.getSpaceAfter();i++) {
						paginator.newRow(new Row(""));
					}
				}
				gi++;
			}
		}		
	}
	
	private static int getKeepHeight(BlockSequence seq, int gi) {
		int keepHeight = seq.getBlock(gi).getSpaceBefore()+seq.getBlock(gi).getRowCount();
		if (seq.getBlock(gi).getKeepWithNext()>0 && gi+1<seq.getBlockCount()) {
			keepHeight += seq.getBlock(gi).getSpaceAfter()+seq.getBlock(gi+1).getSpaceBefore()+seq.getBlock(gi).getKeepWithNext();
			switch (seq.getBlock(gi+1).getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(seq, gi+1);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}
}
