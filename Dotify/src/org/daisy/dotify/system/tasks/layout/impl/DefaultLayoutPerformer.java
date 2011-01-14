package org.daisy.dotify.system.tasks.layout.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

import org.daisy.dotify.system.tasks.layout.flow.BlockProperties;
import org.daisy.dotify.system.tasks.layout.flow.Flow;
import org.daisy.dotify.system.tasks.layout.flow.LayoutException;
import org.daisy.dotify.system.tasks.layout.flow.Leader;
import org.daisy.dotify.system.tasks.layout.flow.Marker;
import org.daisy.dotify.system.tasks.layout.flow.Row;
import org.daisy.dotify.system.tasks.layout.flow.SequenceProperties;
import org.daisy.dotify.system.tasks.layout.flow.SpanProperties;
import org.daisy.dotify.system.tasks.layout.page.LayoutMaster;
import org.daisy.dotify.system.tasks.layout.page.Paginator;
import org.daisy.dotify.system.tasks.layout.text.FilterFactory;
import org.daisy.dotify.system.tasks.layout.utils.BlockHandler;
import org.daisy.dotify.system.tools.StateObject;


/**
 * Breaks flow into rows, page related block properties are left to next step
 * @author Joel Håkansson, TPB
 */
public class DefaultLayoutPerformer implements Flow {
	private int leftMargin;
	private int rightMargin;
	private FlowStruct flowStruct;
	private Stack<BlockProperties> context;
	private boolean firstRow;
	private HashMap<String, LayoutMaster> masters;
	//private final StringFilter filters;
	private Paginator paginator;
	private StateObject state;

	private BlockHandler bh;

	// TODO: fix recursive keep problem
	// TODO: Implement SpanProperites
	// TODO: Implement floating elements
	/**
	 * Create a new flow
	 * @param filtersFactory the filters factory to use
	 */
	public DefaultLayoutPerformer(FilterFactory filtersFactory) {
		this.masters = new HashMap<String, LayoutMaster>();
		//this.filters = builder.filtersFactory.getDefault();
		this.context = new Stack<BlockProperties>();
		this.leftMargin = 0;
		this.rightMargin = 0;
		this.flowStruct = new FlowStruct(); //masters
		this.bh = new BlockHandler(filtersFactory.getDefault());
		this.state = new StateObject();
	}

	public void open(Paginator paginator) {
		state.assertUnopened();
		state.open();
		this.paginator = paginator;
	}

	public void addLayoutMaster(String name, LayoutMaster master) {
		masters.put(name, master);
	}

	//TODO Handle SpanProperites
	public void addChars(CharSequence c, SpanProperties p) {
		state.assertOpen();
		addChars(c);
	}

	// Using BlockHandler
	public void addChars(CharSequence c) {
		state.assertOpen();
		assert context.size()!=0;
		bh.setBlockProperties(context.peek());
		bh.setWidth(masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()).getFlowWidth() - rightMargin);
		ArrayList<Row> ret;
		if (firstRow) {
			ret = bh.layoutBlock(c, leftMargin, masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()));
			firstRow = false;
		} else {
			Row r = flowStruct.getCurrentSequence().getCurrentGroup().popRow();
			ret = bh.appendBlock(c, leftMargin, r, masters.get(flowStruct.getCurrentSequence().getSequenceProperties().getMasterName()));
		}
		for (Row r : ret) {
			flowStruct.getCurrentSequence().getCurrentGroup().pushRow(r);
		}
	}
	// END Using BlockHandler

	public void insertMarker(Marker m) {
		state.assertOpen();
		flowStruct.getCurrentSequence().getCurrentGroup().addMarker(m);
	}

	public void startBlock(BlockProperties p) {
		state.assertOpen();
		assert bh.getCurrentLeader() == null;
		if (context.size()>0) {
			bh.addToBlockIndent(context.peek().getBlockIndent());
			if (context.peek().getListType()!=BlockProperties.ListType.NONE) {
				String listLabel;
				switch (context.peek().getListType()) {
				case OL:
					listLabel = context.peek().nextListNumber()+""; break;
				case UL:
					listLabel = "•";
					break;
				case PL: default:
					listLabel = "";
				}
				bh.setListItem(listLabel, context.peek().getListType());
			}
		}
		FlowGroup c = flowStruct.getCurrentSequence().newFlowGroup();
		c.addSpaceBefore(p.getTopMargin());
		c.setBreakBeforeType(p.getBreakBeforeType());
		c.setKeepType(p.getKeepType());
		c.setKeepWithNext(p.getKeepWithNext());		
		context.push(p);
		leftMargin += p.getLeftMargin();
		rightMargin += p.getRightMargin();
		firstRow = true;
	}
	
	public void endBlock() {
		state.assertOpen();
		/*if (currentLeader!=null) {
			addChars("");
		}*/
		// BlockHandler
		if (bh.getCurrentLeader()!=null || bh.getListItem()!=null) {
			addChars("");
		}
		// BlockHandler
		BlockProperties p = context.pop();
		flowStruct.getCurrentSequence().getCurrentGroup().addSpaceAfter(p.getBottomMargin());
		if (context.size()>0) {
			FlowGroup c = flowStruct.getCurrentSequence().newFlowGroup();
			c.setKeepType(context.peek().getKeepType());
			c.setKeepWithNext(context.peek().getKeepWithNext());
			bh.subtractFromBlockIndent(context.peek().getBlockIndent());
		}
		leftMargin -= p.getLeftMargin();
		rightMargin -= p.getRightMargin();
		firstRow = true;
	}

	public void newSequence(SequenceProperties p) {
		state.assertOpen();
		flowStruct.newSequence(p);
	}

	public void insertLeader(Leader leader) {
		state.assertOpen();
		//currentLeader = leader;
		if (bh.getCurrentLeader()!=null) {
			addChars("");
		}
		bh.setCurrentLeader(leader);
	}
	
	public void newLine() {
		state.assertOpen();
		Row r = new Row("");
		r.setLeftMargin(leftMargin + context.peek().getTextIndent());
		flowStruct.getCurrentSequence().getCurrentGroup().pushRow(r);
	}

	private int getKeepHeight(FlowGroup[] groupA, int gi) {
		int keepHeight = groupA[gi].getSpaceBefore()+groupA[gi].toArray().length;
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
	
	public void close() throws IOException {
		state.assertOpen();
		for (FlowSequence seq : flowStruct.toArray()) {
			if (seq.getSequenceProperties().getInitialPageNumber()==null) {
				paginator.newSequence(masters.get(seq.getSequenceProperties().getMasterName()));
			} else {
				paginator.newSequence(masters.get(seq.getSequenceProperties().getMasterName()), seq.getSequenceProperties().getInitialPageNumber()-1);
			}
			paginator.newPage();
			FlowGroup[] groupA = seq.toArray();
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
				for (Row row : groupA[gi].toArray()) {
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
		state.close();
	}

	public void endFloat() {
		state.assertOpen();
		// TODO Auto-generated method stub
		
	}

	public void insertAnchor(String ref) {
		state.assertOpen();
		// TODO Auto-generated method stub
		
	}

	public void startFloat(String id) {
		state.assertOpen();
		// TODO Auto-generated method stub
		
	}
}
