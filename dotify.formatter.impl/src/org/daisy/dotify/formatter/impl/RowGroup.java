package org.daisy.dotify.formatter.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.daisy.dotify.api.formatter.Marker;
import org.daisy.dotify.api.writer.Row;
import org.daisy.dotify.common.layout.SplitPointUnit;

class RowGroup implements SplitPointUnit {
	private final List<RowImpl> rows;
	private final List<Marker> markers;
	private final List<String> anchors;
	private final float unitSize, lastUnitSize;
	private final boolean breakable, skippable, collapsible, lazyCollapse;
	private final List<String> ids;
	private final String identifier;
	
	static class Builder {
		private final List<RowImpl> rows;
		private List<Marker> markers;
		private List<String> anchors;
		private final float rowDefault;
		private boolean breakable = false, skippable = false, collapsible = false;
		private float overhead = 0;
		private String identifier=null;
		private boolean lazyCollapse = true;
		Builder(float rowDefault, RowImpl ... rows) {
			this(rowDefault, Arrays.asList(rows));
		}
		Builder(float rowDefault) {
			this(rowDefault, new ArrayList<RowImpl>());
		}
		Builder(float rowDefault, List<RowImpl> rows) {
			this.rows = rows;
			this.rowDefault = rowDefault;
			this.markers = new ArrayList<Marker>();
			this.anchors = new ArrayList<String>();
		}
		
		Builder add(RowImpl value) {
			rows.add(value);
			return this;
		}
		Builder addAll(List<RowImpl> value) {
			rows.addAll(value);
			return this;
		}
		Builder markers(List<Marker> value) {
			this.markers = value;
			return this;
		}
		Builder anchors(List<String> value) {
			this.anchors = value;
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
		Builder overhead(float value) {
			this.overhead = value;
			return this;
		}
		Builder identifier(String value) {
			this.identifier = value;
			return this;
		}
		Builder lazyCollapse(boolean value) {
			this.lazyCollapse = value;
			return this;
		}
		RowGroup build() {
			return new RowGroup(this);
		}
	}
	
	private RowGroup(Builder builder) {
		this.rows = builder.rows;
		this.markers = builder.markers;
		this.anchors = builder.anchors;
		this.breakable = builder.breakable;
		this.skippable = builder.skippable;
		this.collapsible = builder.collapsible;
		this.unitSize = calcUnitSize(builder.rowDefault, rows)+builder.overhead;
		this.lastUnitSize = unitSize-(rows.isEmpty()?0:Math.max(0, getRowSpacing(builder.rowDefault, rows.get(rows.size()-1))-1));
		this.ids = new ArrayList<>();
		this.lazyCollapse = builder.lazyCollapse;
		for (RowImpl r : rows) {
			ids.addAll(r.getAnchors());
		}
		this.identifier = builder.identifier;
	}
	
	private static float getRowSpacing(float rowDefault, RowImpl r) {
		return (r.getRowSpacing()!=null?r.getRowSpacing():rowDefault);
	}
	
	private static float calcUnitSize(float rowDefault, List<RowImpl> rows) {
		float t = 0;
		for (RowImpl r : rows) {
			t += getRowSpacing(rowDefault, r);
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
	
	public String getIdentifier() {
		return identifier;
	}

	@Override
	public boolean collapsesWith(Object obj) {
		if (lazyCollapse) {
			return collapsible;
		}
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
		return ids;
	}

	@Override
	public String toString() {
		return "RowGroup [rows=" + rows + ", unitSize=" + unitSize + ", breakable=" + breakable + ", skippable="
				+ skippable + ", collapsible=" + collapsible + ", ids=" + ids + ", identifier=" + identifier + "]";
	}

	@Override
	public float getLastUnitSize() {
		return lastUnitSize;
	}

	public List<Marker> getMarkers() {
		return markers;
	}

	public List<String> getAnchors() {
		return anchors;
	}
	
}
