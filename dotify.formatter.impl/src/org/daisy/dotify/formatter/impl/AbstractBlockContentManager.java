package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.List;

import org.daisy.dotify.api.formatter.Marker;

public abstract class AbstractBlockContentManager implements Iterable<RowImpl> {
	protected boolean isVolatile;
	protected final RowDataProperties rdp;
	protected final FormatterContext fcontext;
	protected final MarginProperties leftParent;
	protected final MarginProperties rightParent;
	protected final MarginProperties leftMargin;
	protected final MarginProperties rightMargin;
	protected final ArrayList<Marker> groupMarkers;
	protected final ArrayList<String> groupAnchors;
	private final List<RowImpl> collapsiblePreContentRows;
	
	AbstractBlockContentManager(RowDataProperties rdp, FormatterContext fcontext) {
		this.leftParent = rdp.getLeftMargin().buildMarginParent(fcontext.getSpaceCharacter());
		this.rightParent = rdp.getRightMargin().buildMarginParent(fcontext.getSpaceCharacter());
		this.leftMargin = rdp.getLeftMargin().buildMargin(fcontext.getSpaceCharacter());
		this.rightMargin = rdp.getRightMargin().buildMargin(fcontext.getSpaceCharacter());
		this.fcontext = fcontext;
		this.rdp = rdp;
		this.groupMarkers = new ArrayList<>();
		this.groupAnchors = new ArrayList<>();
		this.collapsiblePreContentRows = makeCollapsiblePreContentRows(rdp, leftParent, rightParent);
	}
	
	private static List<RowImpl> makeCollapsiblePreContentRows(RowDataProperties rdp, MarginProperties leftParent, MarginProperties rightParent) {
		List<RowImpl> ret = new ArrayList<>();
		for (int i=0; i<rdp.getOuterSpaceBefore();i++) {
			RowImpl row = new RowImpl("", leftParent, rightParent);
			row.setRowSpacing(rdp.getRowSpacing());
			ret.add(row);
		}
		return ret;
	}
	
	public abstract int getRowCount();
	/**
	 * Returns true if this RowDataManager contains objects that makes the formatting volatile,
	 * i.e. prone to change due to for example cross references.
	 * @return returns true if, and only if, the RowDataManager should be discarded if a new pass is requested,
	 * false otherwise
	 */
	public boolean isVolatile() {
		return isVolatile;
	}

	public MarginProperties getLeftMarginParent() {
		return leftParent;
	}

	public MarginProperties getRightMarginParent() {
		return rightParent;
	}

	public List<RowImpl> getCollapsiblePreContentRows() {
		return collapsiblePreContentRows;
	}

	public abstract  List<RowImpl> getInnerPreContentRows();

	/**
	 * Get markers that are not attached to a row, i.e. markers that proceeds any text contents
	 * @return returns markers that proceeds this FlowGroups text contents
	 */
	public ArrayList<Marker> getGroupMarkers() {
		return groupMarkers;
	}
	
	public ArrayList<String> getGroupAnchors() {
		return groupAnchors;
	}
	
	public abstract  List<RowImpl> getPostContentRows();
	public abstract  List<RowImpl> getSkippablePostContentRows();
	//public Iterable<RowImpl> iterable();
	
}
