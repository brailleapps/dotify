package org.daisy.dotify.formatter;

import java.io.IOException;
import java.util.ArrayList;


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
	public static void paginate(BlockStruct fs, Paginator paginator) throws IOException {
		for (BlockSequence seq : fs) {
			if (seq.getSequenceProperties().getInitialPageNumber()==null) {
				paginator.newSequence(fs.getLayoutMaster(seq.getSequenceProperties().getMasterName()));
			} else {
				paginator.newSequence(fs.getLayoutMaster(seq.getSequenceProperties().getMasterName()), seq.getSequenceProperties().getInitialPageNumber()-1);
			}
			paginator.newPage();
			ArrayList<Block> tmp = new ArrayList<Block>();
			for (Block g : seq) {
				tmp.add(g);
			}
			Block[] groupA = new Block[tmp.size()];
			groupA = tmp.toArray(groupA);
			for (int gi = 0; gi<groupA.length; gi++) {
				//int height = ps.getCurrentLayoutMaster().getFlowHeight();
				switch (groupA[gi].getBreakBeforeType()) {
					case PAGE:
						if (paginator.getPageInfo().countRows()>0) {
							paginator.newPage();
						}
						break;
					case AUTO:default:;
				}
				//FIXME: se över recursiv hämtning
				switch (groupA[gi].getKeepType()) {
					case ALL:
						int keepHeight = getKeepHeight(groupA, gi);
						if (paginator.getPageInfo().countRows()>0 && keepHeight>paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows() && keepHeight<=paginator.getPageInfo().getFlowHeight()) {
							paginator.newPage();
						}
						break;
					case AUTO:
						break;
					default:;
				}
				if (groupA[gi].getSpaceBefore()+groupA[gi].getSpaceAfter()>=paginator.getPageInfo().getFlowHeight()) {
					IOException ex = new IOException("Layout exception");
					ex.initCause(new LayoutException("Group margins too large to fit on an empty page."));
					throw ex;
				} else if (groupA[gi].getSpaceBefore()+1>paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows()) {
					paginator.newPage();
				}
				for (int i=0; i<groupA[gi].getSpaceBefore();i++) {
					paginator.newRow(new Row(""));
				}
				paginator.insertMarkers(groupA[gi].getGroupMarkers());
				for (Row row : groupA[gi]) {
					paginator.newRow(row);
				}
				if (groupA[gi].getSpaceAfter()>=paginator.getPageInfo().getFlowHeight()-paginator.getPageInfo().countRows()) {
					paginator.newPage();
				} else {
					for (int i=0; i<groupA[gi].getSpaceAfter();i++) {
						paginator.newRow(new Row(""));
					}
				}
			}
		}		
	}
	
	private static Row[] toArray(Block g) {
		ArrayList<Row> tmp = new ArrayList<Row>();
		for (Row r : g) {
			tmp.add(r);
		}
		Row[] ret = new Row[tmp.size()];
		return tmp.toArray(ret);
	}
	
	private static int getKeepHeight(Block[] groupA, int gi) {
		int keepHeight = groupA[gi].getSpaceBefore()+toArray(groupA[gi]).length;
		if (groupA[gi].getKeepWithNext()>0 && gi+1<groupA.length) {
			keepHeight += groupA[gi].getSpaceAfter()+groupA[gi+1].getSpaceBefore()+groupA[gi].getKeepWithNext();
			switch (groupA[gi+1].getKeepType()) {
				case ALL:
					keepHeight += getKeepHeight(groupA, gi+1);
					break;
				case AUTO: break;
				default:;
			}
		}
		return keepHeight;
	}
}
