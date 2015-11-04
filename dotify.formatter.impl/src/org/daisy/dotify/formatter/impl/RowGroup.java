package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.daisy.dotify.api.writer.Row;
import org.daisy.dotify.common.layout.SplitPointUnit;

class RowGroup implements SplitPointUnit {
	private final List<RowImpl> rows;
	private final float unitSize;
	private final boolean breakable, skippable, collapsible;
	
	static class Builder {
		private final List<RowImpl> rows;
		private final float rowDefault;
		private boolean breakable, skippable, collapsible;
		Builder(float rowDefault, RowImpl ... rows) {
			this(rowDefault, Arrays.asList(rows));
		}
		Builder(float rowDefault, List<RowImpl> rows) {
			this.rows = rows;
			this.rowDefault = rowDefault;
		}
		Builder(float rowDefault) {
			this.rows = new ArrayList<RowImpl>();
			this.rowDefault = rowDefault;
		}
		Builder add(RowImpl row) {
			rows.add(row);
			return this;
		}
		Builder breakable(boolean value) {
			this.breakable = value;
			return this;
		}
		Builder skippable(boolean value) {
			this.skippable = value;
			return this;
		}
		Builder collapsible(boolean value) {
			this.collapsible = value;
			return this;
		}
		RowGroup build() {
			return new RowGroup(this);
		}
	}
	
	private RowGroup(Builder builder) {
		this.rows = builder.rows;
		this.breakable = builder.breakable;
		this.skippable = builder.skippable;
		this.collapsible = builder.collapsible;
		this.unitSize = calcUnitSize(builder.rowDefault, rows);
	}
	
	private static float calcUnitSize(float rowDefault, List<RowImpl> rows) {
		float t = 0;
		for (Row r : rows) {
			t += (r.getRowSpacing()!=null?r.getRowSpacing():rowDefault);
		}
		return t;
	}
	
	List<RowImpl> getRows() {
		return Collections.unmodifiableList(rows);
	}

	@Override
	public boolean isBreakable() {
		return breakable;
	}

	@Override
	public boolean isSkippable() {
		return skippable;
	}

	@Override
	public boolean isCollapsible() {
		return collapsible;
	}

	@Override
	public float getUnitSize() {
		return unitSize;
	}

	@Override
	public boolean collapsesWith(Object obj) {
		if (this == obj) {
			return true;
		} else if (obj == null) {
			return false;
		} else  if (getClass() != obj.getClass()) {
			return false;
		} else {
			RowGroup other = (RowGroup) obj;
			if (this.rows == null) {
				return other.rows == null;
			} else {
				Row a = null;
				for (Row b : this.rows) {
					if (a==null) {
						a = b;
					} else {  
						if (!a.equals(b)) {
							return false;
						}
					}
				}
				for (Row b : other.rows) {
					if (a==null) {
						a = b;
					} else {
						if (!a.equals(b)) {
							return false;
						}
					}
				}
				return true;
			}
		}
	}

	@Override
	public List<String> getSupplementaryIDs() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
