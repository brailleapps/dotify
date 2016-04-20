package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.FormattingTypes.BreakBefore;
import org.daisy.dotify.api.formatter.FormattingTypes.Keep;
import org.daisy.dotify.api.formatter.MarginRegion;

class RowGroupBuilder {
	private final PageSequenceRecorder rec;
	private final LayoutMaster master;
	private final BlockSequence seq;
	private final BlockContext bc;

	RowGroupBuilder(LayoutMaster master, BlockSequence seq, BlockContext blockContext) {
		this.rec = new PageSequenceRecorder();
		this.seq = seq;
		this.master = master;

		//TODO: This assumes that all page templates have margin regions that are of the same width 
		final int mw = getTotalMarginRegionWidth(); 
		bc = new BlockContext(seq.getLayoutMaster().getFlowWidth() - mw, blockContext.getRefs(), blockContext.getContext(), blockContext.getFcontext());
	}
	
	private int getTotalMarginRegionWidth() {
		int mw = 0;
		for (MarginRegion mr : master.getTemplate(1).getLeftMarginRegion()) {
			mw += mr.getWidth();
		}
		for (MarginRegion mr : master.getTemplate(1).getRightMarginRegion()) {
			mw += mr.getWidth();
		}
		return mw;
	}

	private void setProperties(RowGroup.Builder rgb, AbstractBlockContentManager bcm, Block g) {
		if (!"".equals(g.getIdentifier())) { 
			rgb.identifier(g.getIdentifier());
		}
		rgb.markers(bcm.getGroupMarkers());
		rgb.anchors(bcm.getGroupAnchors());
		rgb.keepWithNextSheets(g.getKeepWithNextSheets());
		rgb.keepWithPreviousSheets(g.getKeepWithPreviousSheets());
	}

	List<RowGroupSequence> getResult() {
		for (Block g : seq)  {
			try {
				AbstractBlockContentManager bcm = rec.processBlock(g, bc);

				if (rec.data.isDataGroupsEmpty() || (g.getBreakBeforeType()==BreakBefore.PAGE && !rec.data.isDataEmpty()) || g.getVerticalPosition()!=null) {
					rec.data.newRowGroupSequence(g.getVerticalPosition(), new RowImpl("", bcm.getLeftMarginParent(), bcm.getRightMarginParent()));
					rec.data.keepWithNext = -1;
				}
				List<RowImpl> rl1 = bcm.getCollapsiblePreContentRows();
				if (!rl1.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(master.getRowSpacing(), rl1).
											collapsible(true).skippable(false).breakable(false).build());
				}
				List<RowImpl> rl2 = bcm.getInnerPreContentRows();
				if (!rl2.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(master.getRowSpacing(), rl2).
											collapsible(false).skippable(false).breakable(false).build());
				}
				
				if (bcm.getRowCount()==0) { //TODO: Does this interfere with collapsing margins? 
					if (!bcm.getGroupAnchors().isEmpty() || !bcm.getGroupMarkers().isEmpty() || !"".equals(g.getIdentifier())
							|| g.getKeepWithNextSheets()>0 || g.getKeepWithPreviousSheets()>0 ) {
						RowGroup.Builder rgb = new RowGroup.Builder(master.getRowSpacing(), new ArrayList<RowImpl>());
						setProperties(rgb, bcm, g);
						rec.data.addRowGroup(rgb.build());
					}
				}
	
				int i = 0;
				List<RowImpl> rl3 = bcm.getPostContentRows();
				OrphanWidowControl owc = new OrphanWidowControl(g.getRowDataProperties().getOrphans(),
																g.getRowDataProperties().getWidows(), 
																bcm.getRowCount());
				for (RowImpl r : bcm) {
					i++;
					r.setAdjustedForMargin(true);
					if (i==bcm.getRowCount()) {
						//we're at the last line, this should be kept with the next block's first line
						rec.data.keepWithNext = g.getKeepWithNext();
					}
					RowGroup.Builder rgb = new RowGroup.Builder(master.getRowSpacing()).add(r).
							collapsible(false).skippable(false).breakable(
									r.allowsBreakAfter()&&
									owc.allowsBreakAfter(i-1)&&
									rec.data.keepWithNext<=0 &&
									(Keep.AUTO==g.getKeepType() || i==bcm.getRowCount()) &&
									(i<bcm.getRowCount() || rl3.isEmpty())
									);
					if (i==1) { //First item
						setProperties(rgb, bcm, g);
					}
					rec.data.addRowGroup(rgb.build());
					rec.data.keepWithNext--;
				}
				if (!rl3.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(master.getRowSpacing(), rl3).
						collapsible(false).skippable(false).breakable(rec.data.keepWithNext<0).build());
				}
				List<RowImpl> rl4 = bcm.getSkippablePostContentRows();
				if (!rl4.isEmpty()) {
					rec.data.addRowGroup(new RowGroup.Builder(master.getRowSpacing(), rl4).
						collapsible(true).skippable(true).breakable(rec.data.keepWithNext<0).build());
				}
			} catch (Exception e) {
				rec.invalidateScenario(e);
			}
		}
		rec.finishBlockProcessing();
		return rec.data.getRowGroupSequences();
	}

}