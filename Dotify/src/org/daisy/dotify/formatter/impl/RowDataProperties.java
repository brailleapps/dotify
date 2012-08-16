package org.daisy.dotify.formatter.impl;

import org.daisy.dotify.formatter.dom.CrossReferences;
import org.daisy.dotify.formatter.dom.FormattingTypes;
import org.daisy.dotify.formatter.dom.LayoutMaster;
import org.daisy.dotify.text.StringFilter;

public class RowDataProperties {
	private final int blockIndent, blockIndentParent;
	private final StringFilter filter;
	private final LayoutMaster master;
	private final int leftMargin, rightMargin;
	private final CrossReferences crossReferences;
	private LIDao listProps;
	
	
	public RowDataProperties(StringFilter filter, LayoutMaster master, int blockIndent, int blockIndentParent, int leftMargin, int rightMargin, CrossReferences refs) {
		this.blockIndent = blockIndent;
		this.blockIndentParent = blockIndentParent;
		this.filter = filter;
		this.master = master;
		this.leftMargin = leftMargin;
		this.rightMargin = rightMargin;
		this.listProps = null;
		this.crossReferences = refs;
	}

	public int getBlockIndent() {
		return blockIndent;
	}

	public int getBlockIndentParent() {
		return blockIndentParent;
	}

	public StringFilter getFilter() {
		return filter;
	}

	public LayoutMaster getMaster() {
		return master;
	}

	public int getLeftMargin() {
		return leftMargin;
	}

	public int getRightMargin() {
		return rightMargin;
	}
	
	public void setListItem(String label, FormattingTypes.ListStyle type) {
		listProps = new LIDao(label, type);
	}
	
	public boolean isList() {
		return listProps!=null;
	}
	
	public String getListLabel() {
		return listProps.listLabel;
	}
	
	public FormattingTypes.ListStyle getListStyle() {
		return listProps.listType;
	}
	
	public CrossReferences getCrossReferences() {
		return crossReferences;
	}

	private static class LIDao {
		private final String listLabel;
		private final FormattingTypes.ListStyle listType;
		private LIDao(String label, FormattingTypes.ListStyle type) {
			this.listLabel = label;
			this.listType = type;
		}
	}

}
