package org.daisy.dotify.formatter.impl;

import java.util.Stack;

import org.daisy.dotify.api.formatter.BlockPosition;
import org.daisy.dotify.api.formatter.FormattingTypes;
import org.daisy.dotify.formatter.impl.Segment.SegmentType;

/**
 * Provides a block of rows and the properties
 * associated with it.
 * @author Joel Håkansson
 */

class Block implements Cloneable {
	private String blockId;
	private int spaceBefore;
	private int spaceAfter;
	private FormattingTypes.BreakBefore breakBefore;
	private FormattingTypes.Keep keep;
	private int keepWithNext;
	private int keepWithPreviousSheets;
	private int keepWithNextSheets;
	private String id;
	private Stack<Segment> segments;
	private final RowDataProperties.Builder rdp;
	private BlockContentManager rdm;
	private BlockPosition verticalPosition;
	private SingleLineDecoration leadingDecoration = null;
	private SingleLineDecoration trailingDecoration = null;

	private Integer metaVolume = null, metaPage = null;
	
	Block(String blockId, RowDataProperties.Builder rdp) {
		this.spaceBefore = 0;
		this.spaceAfter = 0;
		this.breakBefore = FormattingTypes.BreakBefore.AUTO;
		this.keep = FormattingTypes.Keep.AUTO;
		this.keepWithNext = 0;
		this.keepWithPreviousSheets = 0;
		this.keepWithNextSheets = 0;
		this.id = "";
		this.blockId = blockId;
		this.segments = new Stack<Segment>();
		this.rdp = rdp;
		this.rdm = null;
		this.verticalPosition = null;
	}
	
	public void addSegment(Segment s) {
		segments.add(s);
	}

	public void addSegment(TextSegment s) {
		if (segments.size() > 0 && segments.peek().getSegmentType() == SegmentType.Text) {
			TextSegment ts = ((TextSegment) segments.peek());
			if (ts.getTextProperties().equals(s.getTextProperties())) {
				// Logger.getLogger(this.getClass().getCanonicalName()).finer("Appending chars to existing text segment.");
				ts.setText(ts.getText() + "" + s.getText());
				return;
			}
		}
		segments.push(s);
	}
	
	public void setListItem(String label, FormattingTypes.ListStyle type) {
		rdp.listProperties(new ListItem(label, type));
	}

	/**
	 * Gets the number of empty rows that should precede the 
	 * rows in this block.
	 * @return returns the number of empty rows preceding the rows in this block
	 */
	public int getSpaceBefore() {
		return spaceBefore;
	}
	
	public int getSpaceAfter() {
		return spaceAfter;
	}
	
	public FormattingTypes.BreakBefore getBreakBeforeType() {
		return breakBefore;
	}
	
	public FormattingTypes.Keep getKeepType() {
		return keep;
	}
	
	public int getKeepWithNext() {
		return keepWithNext;
	}
	
	public int getKeepWithPreviousSheets() {
		return keepWithPreviousSheets;
	}
	
	public int getKeepWithNextSheets() {
		return keepWithNextSheets;
	}
	
	public String getIdentifier() {
		return id;
	}

	public BlockPosition getVerticalPosition() {
		return verticalPosition;
	}
	
	public void addSpaceBefore(int spaceBefore) {
		this.spaceBefore += spaceBefore;
	}
	
	public void addSpaceAfter(int spaceAfter) {
		this.spaceAfter += spaceAfter;
	}
	
	public void setBreakBeforeType(FormattingTypes.BreakBefore breakBefore) {
		this.breakBefore = breakBefore;
	}
	
	public void setKeepType(FormattingTypes.Keep keep) {
		this.keep = keep;
	}
	
	public void setKeepWithNext(int keepWithNext) {
		this.keepWithNext = keepWithNext;
	}
	
	public void setKeepWithPreviousSheets(int keepWithPreviousSheets) {
		this.keepWithPreviousSheets = keepWithPreviousSheets;
	}
	
	public void setKeepWithNextSheets(int keepWithNextSheets) {
		this.keepWithNextSheets = keepWithNextSheets;
	}
	
	public void setIdentifier(String id) {
		this.id = id;
	}

	/**
	 * Gets the vertical position of the block on page, or null if none is
	 * specified
	 */
	public void setVerticalPosition(BlockPosition vertical) {
		this.verticalPosition = vertical;
	}

	public String getBlockIdentifier() {
		return blockId;
	}
	
	public BlockContentManager getBlockContentManager(int flowWidth, CrossReferences refs, DefaultContext context, FormatterContext fcontext) {
		if (rdm==null || rdm.isVolatile()) {
			context.setMetaVolume(metaVolume);
			context.setMetaPage(metaPage);
			rdm = new BlockContentManagerImpl(flowWidth, segments, rdp.build(), refs, context, fcontext);
			context.setMetaVolume(null);
			context.setMetaPage(null);
		}
		return rdm;
	}

	public void setMetaVolume(Integer metaVolume) {
		this.metaVolume = metaVolume;
	}

	public void setMetaPage(Integer metaPage) {
		this.metaPage = metaPage;
	}

	public SingleLineDecoration getLeadingDecoration() {
		return leadingDecoration;
	}
	
	public void setLeadingDecoration(SingleLineDecoration value) {
		this.leadingDecoration = value;
	}
	
	public SingleLineDecoration getTrailingDecoration() {
		return trailingDecoration;
	}

	public void setTrailingDecoration(SingleLineDecoration value) {
		this.trailingDecoration = value;
	}
	
	public RowDataProperties getRowDataProperties() {
		return rdp.build();
	}

    @SuppressWarnings("unchecked")
	public Object clone() {
    	try {
	    	Block newObject = (Block)super.clone();
	    	if (this.segments!=null) {
	    		newObject.segments = (Stack<Segment>)this.segments.clone();
	    	}
	    	return newObject;
    	} catch (CloneNotSupportedException e) { 
    	    // this shouldn't happen, since we are Cloneable
    	    throw new InternalError();
    	}
    }

}
